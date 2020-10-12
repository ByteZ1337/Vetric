package xyz.xenondevs.obfuscator.transformer.renamer

import org.objectweb.asm.commons.ClassRemapper
import org.objectweb.asm.tree.MethodNode
import xyz.xenondevs.obfuscator.asm.DescriptorBasedRemapper
import xyz.xenondevs.obfuscator.jvm.ClassPath
import xyz.xenondevs.obfuscator.jvm.ClassWrapper
import xyz.xenondevs.obfuscator.jvm.JavaArchive
import xyz.xenondevs.obfuscator.supplier.AlphaNumericSupplier
import xyz.xenondevs.obfuscator.supplier.DotsSupplier
import xyz.xenondevs.obfuscator.transformer.Transformer
import xyz.xenondevs.obfuscator.util.ASMUtils
import xyz.xenondevs.obfuscator.util.StringUtils
import xyz.xenondevs.obfuscator.util.flushClose
import java.io.File
import java.util.concurrent.atomic.AtomicInteger

// TODO options: SameName, Dictionary, Mix
// TODO fix
// TODO Search for Reflection calls
object Renamer : Transformer("Renamer") {

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

        val mainClass = findMainClass(jar)
        jar.classes.forEach { clazz ->
            clazz.fields?.let { generateFieldMappings(clazz) }
            clazz.methods?.let { generateMethodMappings(clazz) }
            mappings[clazz.name] = if (clazz != mainClass) DotsSupplier.randomStringUnique() else AlphaNumericSupplier.randomStringUnique()
            println("Generated mappings for " + clazz.name)
        }
    }

    private fun findMainClass(jar: JavaArchive): ClassWrapper? {
        val pluginYaml = jar.getResource("plugin.yml") ?: return null
        val content = pluginYaml.content.decodeToString()
        val className = content.lines().first { it.startsWith("main:", true) }.substring(5).trim()
        return ClassPath.getClassWrapper(className)
    }

    private fun applyMappings(jar: JavaArchive) {
        val remapper = DescriptorBasedRemapper(mappings)
        val newClasses = ArrayList<ClassWrapper>()

        jar.classes.forEach { clazz ->
            val newClass = ClassWrapper(clazz.fileName, jar)
            // FIXME
            clazz.accept(ClassRemapper(newClass, remapper))
            newClass.fileName = "${newClass.name}.class"
            newClasses.add(newClass)
        }

        jar.classes.clear()
        jar.classes.addAll(newClasses)
        ClassPath.reload()
    }

    private fun generateFieldMappings(clazz: ClassWrapper) {
        if (clazz.fields.isEmpty())
            return

        val (names, indexMap) = getDescNames(clazz.fields) { it.desc }

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
        val renameable = clazz.methods.filter { shouldRenameMethod(it, clazz) && !ASMUtils.isInherited(it, clazz) }
        if (renameable.isEmpty())
            return
        val (names, indexMap) = getDescNames(renameable) { it.desc }

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
            list: List<T>,
            mapper: (T) -> String,
    ): Pair<HashSet<String>, HashMap<String, AtomicInteger>> {
        val names = HashSet<String>()
        val indexMap = HashMap<String, AtomicInteger>()
        // Search the amount of needed names and fill the HashSet
        val count = list.groupingBy(mapper).eachCount().values.maxOrNull()!!
        // TODO increase length when no names are left
        repeat(count) { names += DotsSupplier.randomStringUnique(100..200, names) }
        // Fill the HashMap
        list.map(mapper).distinct().forEach { indexMap[it] = AtomicInteger() }

        return names to indexMap
    }
}