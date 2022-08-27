package xyz.xenondevs.vetric.transformer.impl.obfuscation.renamer

import org.objectweb.asm.tree.FieldNode
import org.objectweb.asm.tree.MethodNode
import xyz.xenondevs.bytebase.jvm.ClassWrapper
import xyz.xenondevs.bytebase.jvm.JavaArchive
import xyz.xenondevs.vetric.supplier.SupplierFactory
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
 * TODO
 * - better documentation and general cleanup
 *
 * @param jar The jar to generate mappings for.
 */
class MappingsGenerator(private val jar: JavaArchive, private val renamer: Renamer) {
    
    private val mappings = HashMap<String, String>()
    
    /**
     * Generates mappings for the given [jar].
     */
    fun generateMappings(): HashMap<String, String> {
        mappings.clear()
        if (renamer.renamePackages)
            getPackageMappings(jar, renamer.packageSupplier)
        if (renamer.renameClasses || renamer.renamePackages)
            getClassMappings(jar, renamer.classSupplier)
        jar.classes.forEach { clazz ->
            if (renamer.renameFields && !clazz.fields.isNullOrEmpty())
                getFieldMappings(clazz, renamer.fieldSupplier)
            if (!clazz.methods.isNullOrEmpty()) {
                if (renamer.renameMethods)
                    getMethodMappings(clazz, renamer.methodSupplier)
                if (renamer.renameLocals)
                    getLocalMappings(clazz, renamer.localSupplier)
            }
        }
        return mappings
    }
    
    private fun getPackageMappings(jar: JavaArchive, factory: SupplierFactory) {
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
    
    private fun getClassMappings(jar: JavaArchive, factory: SupplierFactory) {
        if (renamer.removePackages) { // All packages are removed, so we just need a single supplier.
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
                
                if (renamer.renamePackages) {
                    val packageMapping = mappings[packageName]
                        ?: error("Missing package mapping for $packageName")
                    val className = if (renamer.renameClasses) supplier.randomStringUnique() else clazz.className
                    mappings[clazz.name] = "$packageMapping/$className"
                } else {
                    mappings[clazz.name] = "$packageName/${supplier.randomStringUnique()}"
                }
            }
        }
    }
    
    private fun getFieldMappings(clazz: ClassWrapper, factory: SupplierFactory) {
        val renamableFields = clazz.fields.filter(FieldNode::isRenamable)
        
        if (!renamer.repeatNames) { // Unique name for every field.
            val supplier = factory.create(renamableFields.size)
            renamableFields.forEach { field ->
                val newName = supplier.randomStringUnique()
                val fieldPath = field.name + "." + field.desc
                mappings[clazz.name + "." + fieldPath] = newName
                clazz.subClasses.forEach { mappings[it.name + "." + fieldPath] = newName }
            }
        } else { // Same name for fields with the same descriptor.
            val occurrences = getOccurrenceMap(clazz.fields, FieldNode::desc)
            val indexMap = occurrences.mapValues { AtomicInteger(0) }
            val names = getNeededNames(factory, occurrences)
            
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
    
    private fun getMethodMappings(clazz: ClassWrapper, factory: SupplierFactory) {
        val renamableMethods = clazz.methods.filter { it.isRenamable(clazz, mappings) }
        val checker = MethodNameChecker(clazz)
        
        if (!renamer.repeatNames) { // Unique name for every method
            val supplier = factory.create(renamableMethods.size)
            renamableMethods.forEach { method ->
                var newName: String
                do {
                    newName = supplier.randomStringUnique()
                } while (!checker.isUsable(newName, method.desc))
                val methodPath = method.name + method.desc
                mappings[clazz.name + "." + methodPath] = newName
                clazz.subClasses.forEach { mappings[it.name + "." + methodPath] = newName }
            }
        } else {
            val occurrences = getOccurrenceMap(renamableMethods, MethodNode::desc)
            val indexMap = occurrences.mapValues { AtomicInteger(0) }
            val names = getNeededNames(factory, occurrences, checker)
            
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
    
    private fun getLocalMappings(clazz: ClassWrapper, factory: SupplierFactory) {
        val toProcess = clazz.methods.filter { it.localVariables != null }
        
        toProcess.forEach { method ->
            if (renamer.repeatNames) { // Same name for all locals, descriptor can be ignored.
                val name = factory.create(1).randomString()
                method.localVariables.forEach { local ->
                    mappings[clazz.name + '.' + method.name + method.desc + '.' + local.name + '.' + local.desc] = name
                }
            } else { // Unique name for every local.
                val generated = HashSet<String>()
                val supplier = factory.create(method.localVariables.size)
                method.localVariables.forEach { local ->
                    val path = clazz.name + '.' + method.name + method.desc + '.' + local.name + '.' + local.desc
                    mappings[path] = supplier.randomStringUnique(generated)
                }
            }
            
        }
    }
    
    private fun <T> getOccurrenceMap(list: List<T>, mapper: (T) -> String): Map<String, Int> =
        list.groupingBy(mapper).eachCount()
    
    private fun getNeededNames(factory: SupplierFactory, occurrences: Map<String, Int>): List<String> {
        // Get the maximum amount of names needed.
        val amount = occurrences.values.maxOrNull() ?: return emptyList()
        val supplier = factory.create(amount)
        
        val names = HashSet<String>()
        repeat(amount) { names += supplier.randomStringUnique(names) }
        return names.toList()
    }
    
    private fun getNeededNames(factory: SupplierFactory, occurrences: Map<String, Int>, checker: MethodNameChecker): List<String> {
        // Get the maximum amount of names needed.
        val amount = occurrences.values.maxOrNull() ?: return emptyList()
        val supplier = factory.create(amount)
        
        val names = HashSet<String>()
        while (names.size < amount) {
            val name = supplier.randomStringUnique(names)
            if (checker.isUsable(name))
                names += name
        }
        return names.toList()
    }
    
    private inner class MethodNameChecker(clazz: ClassWrapper) {
        private val methodNames = HashSet<String>()
        private val methodPaths = HashSet<String>()
        
        init {
            clazz.subClasses.asSequence()
                .flatMap { it.superClasses + it }
                .distinctBy(ClassWrapper::name)
                .forEach { c ->
                    c.methods.forEach { method ->
                        val fullPath = clazz.name + "." + method.name + method.desc
                        if (fullPath in mappings) {
                            val newName = mappings[fullPath]!!
                            methodNames += newName
                            methodPaths += newName + method.desc
                        } else {
                            methodNames += method.name
                            methodPaths += method.name + method.desc
                        }
                    }
                }
        }
        
        fun isUsable(name: String) =
            name !in methodNames
        
        fun isUsable(name: String, desc: String) =
            name + desc !in methodPaths
        
    }
    
}