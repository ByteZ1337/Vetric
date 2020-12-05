package xyz.xenondevs.obfuscator.transformers.renamer

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import org.objectweb.asm.commons.ClassRemapper
import org.objectweb.asm.tree.FieldNode
import org.objectweb.asm.tree.MethodNode
import xyz.xenondevs.obfuscator.asm.DescriptorBasedRemapper
import xyz.xenondevs.obfuscator.config.type.SupplierType
import xyz.xenondevs.obfuscator.config.type.TransformerType
import xyz.xenondevs.obfuscator.jvm.ClassPath
import xyz.xenondevs.obfuscator.jvm.ClassWrapper
import xyz.xenondevs.obfuscator.jvm.JavaArchive
import xyz.xenondevs.obfuscator.suppliers.AlphaSupplier
import xyz.xenondevs.obfuscator.suppliers.StringSupplier
import xyz.xenondevs.obfuscator.transformers.Transformer
import xyz.xenondevs.obfuscator.utils.ASMUtils
import xyz.xenondevs.obfuscator.utils.between
import xyz.xenondevs.obfuscator.utils.flushClose
import xyz.xenondevs.obfuscator.utils.json.getBoolean
import java.io.File
import java.util.concurrent.atomic.AtomicInteger

// TODO search for reflection calls, package renaming
object Renamer : Transformer("Renamer", RenamerConfig) {
    
    val SMAP_REGEX = Regex("^SMAP .+\\.kt Kotlin .*")
    
    var packagesSupplier: StringSupplier = AlphaSupplier()
    var classesSupplier: StringSupplier = AlphaSupplier()
    var fieldsSupplier: StringSupplier = AlphaSupplier()
    var methodsSupplier: StringSupplier = AlphaSupplier()
    
    var repeatNames = true
    var removePackages = true
    var renamePackages = false
    var renameClasses = true
    var renameFields = true
    var renameMethods = true
    
    val mappings = HashMap<String, String>()
    
    override fun transformJar(jar: JavaArchive) {
        generateMappings(jar)
        applyMappings(jar)
        
        val mappingsFile = File("mappings.txt")
        val writer = mappingsFile.bufferedWriter()
        mappings.toList().sortedBy { it.first }.forEach { writer.write("${it.first} -> ${it.second}\n") }
        writer.flushClose()
    }
    
    private fun generateMappings(jar: JavaArchive) {
        ClassPath.buildJarTree(jar)
        mappings.clear()
        
        jar.classes.forEach { clazz ->
            if (renameFields && !clazz.fields.isNullOrEmpty())
                generateFieldMappings(clazz)
            if (renameMethods && !clazz.methods.isNullOrEmpty())
                generateMethodMappings(clazz)
            if (renameClasses)
                mappings[clazz.name] = classesSupplier.randomStringUnique()
            if (renameFields || renameMethods || renameClasses)
                println("Generated mappings for " + clazz.name)
        }
    }
    
    private fun generateFieldMappings(clazz: ClassWrapper) {
        val (names, indexMap) = getDescNames(fieldsSupplier, clazz.fields, FieldNode::desc)
        
        clazz.fields.forEach { field ->
            if (clazz.isEnum() && "\$VALUES" == field.name)
                return@forEach
            
            // Get the new name with the current index of the descriptor.
            val newName = names.toList()[indexMap[field.desc]!!.getAndIncrement()]
            // Add the path to the mappings HashMap
            mappings["${clazz.name}.${field.name}.${field.desc}"] = newName
            clazz.getFullSubClasses().forEach { mappings["$it.${field.name}.${field.desc}"] = newName }
        }
    }
    
    private fun generateMethodMappings(clazz: ClassWrapper) {
        // TODO replace with putIfAbsent
        val renameable = clazz.methods.filter { shouldRenameMethod(it, clazz) && !ASMUtils.isInherited(it, clazz) }
        if (renameable.isEmpty())
            return
        val (names, indexMap) = getDescNames(methodsSupplier, renameable, MethodNode::desc)
        
        renameable.forEach { method ->
            // Get the new name with the current index of the descriptor.
            val newName = names.toList()[indexMap[method.desc]!!.getAndIncrement()]
            // Add the path to the mappings HashMap
            mappings["${clazz.name}.${method.name}${method.desc}"] = newName
            clazz.getFullSubClasses().forEach { mappings["$it.${method.name}${method.desc}"] = newName }
        }
    }
    
    private fun shouldRenameMethod(method: MethodNode, owner: ClassWrapper) =
        // Don't rename native methods
        !ASMUtils.isNative(method.access) &&
            // Don't rename <clinit> and <init>
            !method.name.startsWith('<')
            // Don't rename main and agent main methods
            && "main" != method.name && "premain" != method.name
            // Don't rename enum static methods
            && !(owner.isEnum() && ASMUtils.isStatic(method.access) && ("values" == method.name || "valueOf" == method.name))
    
    private fun <T> getDescNames(
        supplier: StringSupplier,
        list: List<T>,
        mapper: (T) -> String
    ): Pair<HashSet<String>, HashMap<String, AtomicInteger>> {
        val names = HashSet<String>()
        val indexMap = HashMap<String, AtomicInteger>()
        // Search the amount of needed names and fill the HashSet
        val count = list.groupingBy(mapper).eachCount().values.maxOrNull()!!
        // TODO increase length when no names are left
        repeat(count) { names += supplier.randomStringUnique(names) }
        // Fill the HashMap
        list.map(mapper).distinct().forEach { indexMap[it] = AtomicInteger() }
        
        return names to indexMap
    }
    
    private fun applyMappings(jar: JavaArchive) {
        val remapper = DescriptorBasedRemapper(mappings)
        val newClasses = ArrayList<ClassWrapper>()
        
        jar.classes.forEach { clazz ->
            val newClass = ClassWrapper(clazz.fileName)
            // FIXME?
            clazz.accept(ClassRemapper(newClass, remapper))
            newClass.fileName = "${newClass.name}.class"
            processDebug(newClass)
            newClasses.add(newClass)
        }
        
        jar.classes.clear()
        jar.classes.addAll(newClasses)
        ClassPath.reload()
    }
    
    private fun processDebug(clazz: ClassWrapper) {
        if (!clazz.sourceFile.isNullOrBlank())
            clazz.sourceFile = "${clazz.className}.java"
        if (!clazz.sourceDebug.isNullOrBlank() && clazz.sourceDebug.matches(SMAP_REGEX)) {
            val originalname = clazz.originalName.substringBeforeLast('.')
            val originalClassName = clazz.originalName.between('/', '.')
            clazz.sourceDebug = clazz.sourceDebug
                .replace(clazz.name, originalname)
                .replace(clazz.className, originalClassName)
        }
    }
    
    private object RenamerConfig : TransformerType(Renamer::class) {
        
        override fun parse(obj: JsonObject) {
            super.parse(obj)
            if (!enabled)
                return
            
            if (obj.has("supplier")) {
                val supplierElement = obj["supplier"]
                when {
                    SupplierType.isValid(supplierElement, true) -> handleSingleSupplier(supplierElement)
                    supplierElement is JsonObject -> handleMultipleSuppliers(supplierElement)
                    else -> error("Invalid element for Renamer supplier.")
                }
            }
            repeatNames = obj.getBoolean("repeatnames", false)
            removePackages = obj.getBoolean("removepackages", true)
            renamePackages = obj.getBoolean("packages", false)
            renameClasses = obj.getBoolean("classes", true)
            renameFields = obj.getBoolean("fields", true)
            renameMethods = obj.getBoolean("methods", true)
            
            if (renamePackages && removePackages) {
                println("RenamePackages and RemovePackages is set to true. Defaulting to removing packages")
                renamePackages = false
            }
        }
        
        private fun handleSingleSupplier(element: JsonElement) {
            val supplier = SupplierType.parseElement(element)
            packagesSupplier = supplier
            classesSupplier = supplier
            fieldsSupplier = supplier
            methodsSupplier = supplier
        }
        
        private fun handleMultipleSuppliers(obj: JsonObject) {
            if (obj.has("packages") && SupplierType.isValid(obj["packages"]))
                packagesSupplier = SupplierType.parseElement(obj["packages"])
            if (obj.has("classes") && SupplierType.isValid(obj["classes"]))
                classesSupplier = SupplierType.parseElement(obj["classes"])
            if (obj.has("fields") && SupplierType.isValid(obj["fields"]))
                fieldsSupplier = SupplierType.parseElement(obj["fields"])
            if (obj.has("methods") && SupplierType.isValid(obj["methods"]))
                methodsSupplier = SupplierType.parseElement(obj["methods"])
        }
    }
}