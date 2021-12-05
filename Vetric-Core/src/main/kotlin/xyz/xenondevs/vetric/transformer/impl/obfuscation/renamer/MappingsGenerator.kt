package xyz.xenondevs.vetric.transformer.impl.obfuscation.renamer

import org.objectweb.asm.tree.FieldNode
import org.objectweb.asm.tree.MethodNode
import xyz.xenondevs.bytebase.jvm.ClassWrapper
import xyz.xenondevs.bytebase.jvm.JavaArchive
import xyz.xenondevs.vetric.supplier.StringSupplier
import java.util.concurrent.atomic.AtomicInteger

/**
 * Generates mappings for the given [jar].
 *
 * ## RepeatNames logic
 * This class also implements the repeatNames logic needed by the Renamer. repeatNames is a boolean
 * that determines whether the names of the fields should be repeated for different descriptors.
 *
 * Simple explanation of the repeatNames logic:
 * ```java
 * public int field1 = 0;
 * public int field2 = 0;
 * public int field3 = "";
 * public int field4 = new Object();
 * ```
 * Without repeatNames, the mappings would be:
 * ```
 * field1 -> a
 * field2 -> b
 * field3 -> c
 * field4 -> d
 * ```
 * With repeatNames, the mappings would be:
 * ```
 * field1 -> a
 * field2 -> b
 * field3 -> a
 * field4 -> a
 * ```
 * This is possible because all references to fields and methods contain the descriptor in bytecode. So the JVM can
 * distinguish them, even if they have the same name.
 *
 * ## Package renaming
 * All directories in the jar get a mapping entry even if they only contain resources. Instead of implementing
 * some complicated logic to determine whether a directory contains resources, we simply add a mapping entry for
 * every directory then remove empty directories in [JavaArchive.write].
 *
 * ## TODO
 * - Some sort of mappings config instead of accessing Renamer fields.
 * - Store mappings in a field instead of always passing them via parameters.
 * - Use different indexes/generated HashSets for each package depth.
 *
 * @param jar The jar to generate mappings for.
 */
class MappingsGenerator(private val jar: JavaArchive) {
    
    /**
     * Generates mappings for the given [jar].
     */
    fun generateMappings(): HashMap<String, String> {
        val mappings = HashMap<String, String>()
        if (Renamer.renamePackages)
            getPackageMappings(jar, Renamer.packageSupplier, mappings)
        jar.classes.forEach { clazz ->
            if (Renamer.renameClasses)
                mappings[clazz.name] = getClassMapping(clazz, Renamer.classSupplier, mappings)
            if (Renamer.renameFields && !clazz.fields.isNullOrEmpty())
                getFieldMappings(clazz, Renamer.fieldSupplier, mappings)
            if (!clazz.methods.isNullOrEmpty()) {
                if (Renamer.renameMethods)
                    getMethodMappings(clazz, Renamer.methodSupplier, mappings)
                if (Renamer.renameLocals)
                    getLocalMappings(clazz, Renamer.localSupplier, mappings)
            }
        }
        return mappings
    }
    
    private fun getPackageMappings(jar: JavaArchive, supplier: StringSupplier, mappings: HashMap<String, String>) {
        jar.directories.sortedBy { it.length }.forEach {
            val path = it.dropLast(1)
            val packages = path.split('/')
            if (packages.size > 1) {
                val prefix = mappings[path.substringBeforeLast('/')] ?: error("Missing package prefix for $path")
                mappings[path] = "$prefix/" + supplier.randomStringUnique()
            } else {
                mappings[path] = supplier.randomStringUnique()
            }
            jar.directories.add(mappings[path]!!)
        }
    }
    
    private fun getClassMapping(clazz: ClassWrapper, supplier: StringSupplier, mappings: HashMap<String, String>): String {
        if (Renamer.renamePackages) {
            val name = clazz.name
            val packageName = name.substringBeforeLast('/')
            val packageMapping = mappings[packageName]
            if (packageMapping != null)
                return "$packageMapping/${supplier.randomStringUnique()}"
        }
        return supplier.randomStringUnique()
    }
    
    private fun getFieldMappings(clazz: ClassWrapper, supplier: StringSupplier, mappings: HashMap<String, String>) {
        val renamableFields = clazz.fields.filter(FieldNode::isRenamable)
        
        if (!Renamer.repeatNames) { // Unique name for every field.
            val generated = HashSet<String>()
            renamableFields.forEach { field ->
                val newName = supplier.randomStringUnique(generated)
                val fieldPath = field.name + "." + field.desc
                mappings[clazz.name + "." + fieldPath] = newName
                clazz.subClasses.forEach { mappings[it.name + "." + fieldPath] = newName }
            }
        } else { // Same name for fields with the same descriptor.
            val occurrences = getOccurrenceMap(clazz.fields, FieldNode::desc)
            val indexMap = occurrences.mapValues { AtomicInteger(0) }
            val names = getNeededNames(supplier, occurrences)
            
            renamableFields.forEach { field ->
                // Get the current index of the descriptor, then increase the index.
                val index = indexMap[field.desc]!!.getAndIncrement()
                // Use the index to retrieve the current name.
                val newName = names[index]
                
                // Add the new name to the mappings.
                val fieldPath = field.name + '.' + field.desc
                mappings["${clazz.name}.$fieldPath"] = newName
                clazz.subClasses.forEach { mappings[it.name + "." + fieldPath] = newName }
            }
        }
    }
    
    private fun getMethodMappings(clazz: ClassWrapper, supplier: StringSupplier, mappings: HashMap<String, String>) {
        val renamableMethods = clazz.methods.filter { it.isRenamable(clazz/*, mappings*/) }
        
        if (!Renamer.repeatNames) { // Unique name for every method
            val generated = HashSet<String>()
            renamableMethods.forEach { method ->
                val newName = supplier.randomStringUnique(generated)
                val methodPath = method.name + method.desc
                mappings[clazz.name + "." + methodPath] = newName
                clazz.subClasses.forEach { mappings[it.name + "." + methodPath] = newName }
            }
        } else {
            val occurrences = getOccurrenceMap(renamableMethods, MethodNode::desc)
            val indexMap = occurrences.mapValues { AtomicInteger(0) }
            val names = getNeededNames(supplier, occurrences)
            
            renamableMethods.forEach { method ->
                // Get the current index of the descriptor, then increase the index.
                val index = indexMap[method.desc]!!.getAndIncrement()
                // Use the index to retrieve the current name.
                val newName = names[index]
                
                // Add the new name to the mappings.
                val methodPath = method.name + method.desc
                mappings["${clazz.name}.$methodPath"] = newName
                clazz.subClasses.forEach { mappings[it.name + "." + methodPath] = newName }
            }
        }
    }
    
    private fun getLocalMappings(clazz: ClassWrapper, supplier: StringSupplier, mappings: HashMap<String, String>) {
        val toProcess = clazz.methods.filter { it.localVariables != null }
        
        toProcess.forEach { method ->
            if (Renamer.repeatNames) { // Same name for all locals, descriptor can be ignored.
                val name = supplier.randomString()
                method.localVariables.forEach { local ->
                    mappings[clazz.name + '.' + method.name + method.desc + '.' + local.name + '.' + local.desc] = name
                }
            } else { // Unique name for every local.
                val generated = HashSet<String>()
                method.localVariables.forEach { local ->
                    val path = clazz.name + '.' + method.name + method.desc + '.' + local.name + '.' + local.desc
                    mappings[path] = supplier.randomStringUnique(generated)
                }
            }
            
        }
    }
    
    /**
     * Generates a map of descriptors and their occurrences in the given list.
     *
     * For example (with the above fields):
     * ```
     * "I" -> 2
     * "Ljava/lang/String;" -> 1
     * "Ljava/lang/Object;" -> 1
     * ```
     */
    private fun <T> getOccurrenceMap(list: List<T>, mapper: (T) -> String): Map<String, Int> =
        list.groupingBy(mapper).eachCount()
    
    /**
     * Generates a list of names using the [supplier] that are needed for the given [occurrences].
     */
    private fun getNeededNames(supplier: StringSupplier, occurrences: Map<String, Int>): List<String> {
        // Get the maximum amount of names needed.
        val amount = occurrences.values.maxOrNull() ?: return emptyList()
        
        val names = HashSet<String>()
        repeat(amount) { names += supplier.randomStringUnique(names) }
        return names.toList()
    }
    
}