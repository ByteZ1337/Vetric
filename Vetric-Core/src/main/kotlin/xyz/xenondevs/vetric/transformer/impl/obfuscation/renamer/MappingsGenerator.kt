package xyz.xenondevs.vetric.transformer.impl.obfuscation.renamer

import org.objectweb.asm.tree.FieldNode
import org.objectweb.asm.tree.MethodNode
import xyz.xenondevs.bytebase.jvm.ClassWrapper
import xyz.xenondevs.bytebase.jvm.JavaArchive
import xyz.xenondevs.vetric.supplier.StringSupplier
import xyz.xenondevs.vetric.supplier.SupplierFactory
import xyz.xenondevs.vetric.transformer.impl.obfuscation.renamer.Renamer.classSupplier
import xyz.xenondevs.vetric.transformer.impl.obfuscation.renamer.Renamer.fieldSupplier
import xyz.xenondevs.vetric.transformer.impl.obfuscation.renamer.Renamer.localSupplier
import xyz.xenondevs.vetric.transformer.impl.obfuscation.renamer.Renamer.methodSupplier
import xyz.xenondevs.vetric.transformer.impl.obfuscation.renamer.Renamer.packageSupplier
import xyz.xenondevs.vetric.transformer.impl.obfuscation.renamer.Renamer.removePackages
import xyz.xenondevs.vetric.transformer.impl.obfuscation.renamer.Renamer.renameClasses
import xyz.xenondevs.vetric.transformer.impl.obfuscation.renamer.Renamer.renameFields
import xyz.xenondevs.vetric.transformer.impl.obfuscation.renamer.Renamer.renameLocals
import xyz.xenondevs.vetric.transformer.impl.obfuscation.renamer.Renamer.renameMethods
import xyz.xenondevs.vetric.transformer.impl.obfuscation.renamer.Renamer.renamePackages
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
 * All directories in the jar get a mapping entry if they contain at least one class. Instead of implementing
 * some complicated logic to determine whether a directory still contains resources, we simply let ByteBase remove empty
 * directories in [JavaArchive.write].
 *
 * @param jar The jar to generate mappings for.
 */
class MappingsGenerator(private val jar: JavaArchive) {
    
    /**
     * Generates mappings for the given [jar].
     */
    fun generateMappings(): HashMap<String, String> {
        val mappings = HashMap<String, String>()
        if (renamePackages)
            getPackageMappings(jar, packageSupplier, mappings)
        if (renameClasses || renamePackages)
            getClassMappings(jar, classSupplier, mappings)
        jar.classes.forEach { clazz ->
            if (renameFields && !clazz.fields.isNullOrEmpty())
                getFieldMappings(clazz, fieldSupplier.create(), mappings)
            if (!clazz.methods.isNullOrEmpty()) {
                if (renameMethods)
                    getMethodMappings(clazz, methodSupplier.create(), mappings)
                if (renameLocals)
                    getLocalMappings(clazz, localSupplier.create(), mappings)
            }
        }
        return mappings
    }
    
    private fun getPackageMappings(jar: JavaArchive, factory: SupplierFactory, mappings: HashMap<String, String>) {
        val depthCount = jar.packages.groupBy { it.split('/').size - 1 }.mapValues { it.value.size }
        val suppliers = Array(depthCount.size) { factory.create(depthCount[it + 1]!!) }
        
        jar.packages.sortedBy { it.length }.forEach {
            val path = it.dropLast(1)
            val depth = path.split('/').size - 1
            if (depth > 0) {
                val prefix = mappings[path.substringBeforeLast('/')] ?: error("Missing package prefix for $path")
                mappings[path] = "$prefix/" + suppliers[depth].randomStringUnique()
            } else {
                mappings[path] = suppliers[depth].randomStringUnique()
            }
            jar.directories.add(mappings[path]!!)
        }
    }
    
    private fun getClassMappings(jar: JavaArchive, factory: SupplierFactory, mappings: HashMap<String, String>) {
        if (removePackages) { // All packages are removed, so we just need a single supplier.
            val supplier = factory.create(jar.classes.size)
            jar.classes.forEach { clazz ->
                mappings[clazz.name] = supplier.randomStringUnique()
            }
        } else {
            val packageSuppliers = jar.packages
                .map { pkg -> pkg.dropLast(1) to jar.classes.count { it.name == pkg + it.className } } // Count classes in package
                .filter { it.second > 0 } // Only packages with classes
                .associate { it.first to factory.create(it.second) } // Create a supplier for each package
            
            jar.classes.forEach { clazz ->
                val packageName = clazz.name.substringBeforeLast('/')
                val supplier = packageSuppliers[packageName]
                    ?: error("Missing package supplier for $packageName")
                
                if (renamePackages) {
                    val packageMapping = mappings[packageName]
                        ?: error("Missing package mapping for $packageName")
                    val className = if (renameClasses) supplier.randomStringUnique() else clazz.className
                    mappings[clazz.name] = "$packageMapping/$className"
                } else {
                    mappings[clazz.name] = "$packageName/${supplier.randomStringUnique()}"
                }
            }
        }
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
        val renamableMethods = clazz.methods.filter { it.isRenamable(clazz, mappings) }
        
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
                val newName = names[index] // TODO: Check if this name overrides an already existing name of this class or a super class.
                
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
    
    private fun <T> getOccurrenceMap(list: List<T>, mapper: (T) -> String): Map<String, Int> =
        list.groupingBy(mapper).eachCount()
    
    private fun getNeededNames(supplier: StringSupplier, occurrences: Map<String, Int>): List<String> {
        // Get the maximum amount of names needed.
        val amount = occurrences.values.maxOrNull() ?: return emptyList()
        
        val names = HashSet<String>()
        repeat(amount) { names += supplier.randomStringUnique(names) }
        return names.toList()
    }
    
}