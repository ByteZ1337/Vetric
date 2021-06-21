package xyz.xenondevs.vetric.transformer.obfuscation.renamer

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import xyz.xenondevs.vetric.asm.Refactorer
import xyz.xenondevs.vetric.config.type.SupplierType
import xyz.xenondevs.vetric.config.type.TransformerConfig
import xyz.xenondevs.vetric.jvm.ClassPath
import xyz.xenondevs.vetric.jvm.JavaArchive
import xyz.xenondevs.vetric.supplier.AlphaSupplier
import xyz.xenondevs.vetric.supplier.StringSupplier
import xyz.xenondevs.vetric.transformer.Transformer
import xyz.xenondevs.vetric.util.flushClose
import xyz.xenondevs.vetric.util.json.getBoolean
import java.io.File

// TODO search for reflection calls, package renaming
object Renamer : Transformer("Renamer", RenamerConfig) {
    
    private val SMAP_REGEX = Regex("^SMAP .+\\.kt Kotlin .*")
    
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
    
    var mappings: MutableMap<String, String> = HashMap()
    
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
        mappings = MappingsGenerator(jar).generateMappings()
    }
    
    private fun applyMappings(jar: JavaArchive) {
        val newClasses = Refactorer(jar, mappings).applyMappings()
        jar.classes.clear()
        jar.classes.addAll(newClasses)
        ClassPath.reload()
    }
    
    private object RenamerConfig : TransformerConfig(Renamer::class) {
        
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