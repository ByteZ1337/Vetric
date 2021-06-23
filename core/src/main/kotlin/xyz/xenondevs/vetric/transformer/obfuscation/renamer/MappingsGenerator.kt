package xyz.xenondevs.vetric.transformer.obfuscation.renamer

import org.objectweb.asm.tree.FieldNode
import org.objectweb.asm.tree.MethodNode
import xyz.xenondevs.vetric.exclusion.ExclusionManager
import xyz.xenondevs.vetric.jvm.ClassWrapper
import xyz.xenondevs.vetric.jvm.JavaArchive
import xyz.xenondevs.vetric.supplier.StringSupplier
import xyz.xenondevs.vetric.util.asm.ASMUtils
import java.util.concurrent.atomic.AtomicInteger

class MappingsGenerator(private val jar: JavaArchive) {
    
    fun generateMappings(): MutableMap<String, String> {
        val mappings = HashMap<String, String>()
        jar.classes.filterNot(ExclusionManager::isExcluded).forEach { clazz ->
            if (Renamer.renameClasses)
                mappings[clazz.name] = Renamer.classesSupplier.randomStringUnique()
            if (Renamer.renameFields && !clazz.fields.isNullOrEmpty())
                generateFieldMappings(clazz, Renamer.fieldsSupplier, mappings)
            if (!clazz.methods.isNullOrEmpty()) {
                if (Renamer.renameMethods)
                    generateMethodMappings(clazz, Renamer.methodsSupplier, mappings)
                if (Renamer.renameLocalVariables)
                    generateLocalVariableMappings(clazz, Renamer.localVariablesSupplier, mappings)
            }
        }
        return mappings
    }
    
    private fun generateFieldMappings(clazz: ClassWrapper, supplier: StringSupplier, mappings: MutableMap<String, String>) {
        val renameableFields = clazz.fields.filter { ASMUtils.isRenameable(it, clazz) }
        
        // Unique name for every field.
        if (!Renamer.repeatNames) {
            val generated = HashSet<String>()
            renameableFields.forEach { field ->
                val newName = supplier.randomStringUnique(generated)
                val fieldPath = field.name + '.' + field.desc
                mappings["${clazz.name}.$fieldPath"] = newName
                clazz.getFullSubClasses().forEach { mappings["$it.$fieldPath"] = newName }
            }
            return
        }
        
        // Fields with different descriptors will get the same name.
        val occurrenceMap = getOccurrenceMap(clazz.fields, FieldNode::desc)
        val indexMap = occurrenceMap.mapValues { AtomicInteger(0) }
        val nameList = getNeededNames(supplier, occurrenceMap)
        renameableFields.forEach { field ->
            // Get the current index of the descriptor, then increase the index.
            val index = indexMap[field.desc]!!.getAndIncrement()
            // Use the index to retrieve the current name.
            val newName = nameList[index]
            
            // Add the new name to the mappings.
            val fieldPath = field.name + '.' + field.desc
            mappings["${clazz.name}.$fieldPath"] = newName
            clazz.getFullSubClasses().forEach { mappings["$it.$fieldPath"] = newName }
        }
    }
    
    private fun generateMethodMappings(clazz: ClassWrapper, supplier: StringSupplier, mappings: MutableMap<String, String>) {
        val renameableMethods = clazz.methods.filter { ASMUtils.isRenameable(it, clazz) }
        
        // Unique name for every method.
        if (!Renamer.repeatNames) {
            val generated = HashSet<String>()
            renameableMethods.forEach { method ->
                val newName = supplier.randomStringUnique(generated)
                mappings[clazz.name + '.' + method.name + method.desc] = newName
                clazz.getFullSubClasses().forEach { mappings["$it.${method.name}${method.desc}"] = newName }
            }
            return
        }
        
        // Methods with different descriptors will get the same name.
        val occurrenceMap = getOccurrenceMap(clazz.methods, MethodNode::desc)
        val indexMap = occurrenceMap.mapValues { AtomicInteger(0) }
        val nameList = getNeededNames(supplier, occurrenceMap)
        renameableMethods.forEach { method ->
            // Get the current index of the descriptor, then increase the index.
            val index = indexMap[method.desc]!!.getAndIncrement()
            // Use the index to retrieve the current name.
            val newName = nameList[index]
            
            // Add the new name to the mappings.
            val methodPath = clazz.name + '.' + method.name + method.desc
            mappings[methodPath] = newName
            clazz.getFullSubClasses().forEach { mappings["$it.${method.name}${method.desc}"] = newName }
        }
    }
    
    private fun generateLocalVariableMappings(clazz: ClassWrapper, supplier: StringSupplier, mappings: MutableMap<String, String>) {
        val toProcess = clazz.methods.filter { !ExclusionManager.isExcluded(clazz, it) && it.localVariables != null }
        
        toProcess.forEach { method ->
            if (Renamer.repeatNames) { // All variables get the same name
                val name = supplier.randomString()
                method.localVariables?.forEach { v ->
                    val path = clazz.name + '.' + method.name + method.desc + '.' + v.name + '.' + v.desc
                    mappings[path] = name
                }
            } else { // All variables get different names
                val generated = HashSet<String>()
                method.localVariables?.forEach { v ->
                    val path = clazz.name + '.' + method.name + method.desc + '.' + v.name + '.' + v.desc
                    mappings[path] = supplier.randomStringUnique(generated)
                }
            }
        }
    }
    
    private fun <T> getOccurrenceMap(list: List<T>, mapper: (T) -> String): Map<String, Int> =
        list.groupingBy(mapper).eachCount()
    
    private fun getNeededNames(supplier: StringSupplier, occurrenceMap: Map<String, Int>): List<String> {
        // Get the max occurrences of all descriptors
        val amount = occurrenceMap.values.maxOrNull() ?: return emptyList()
        
        // Generate needed amount of names.
        val names = HashSet<String>()
        repeat(amount) { names += supplier.randomStringUnique(names) }
        return names.toList()
    }
    
}