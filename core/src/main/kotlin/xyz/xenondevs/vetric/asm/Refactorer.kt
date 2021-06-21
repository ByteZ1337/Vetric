package xyz.xenondevs.vetric.asm

import org.objectweb.asm.commons.ClassRemapper
import org.objectweb.asm.commons.SimpleRemapper
import xyz.xenondevs.vetric.jvm.ClassWrapper
import xyz.xenondevs.vetric.jvm.JavaArchive

class Refactorer(private val jar: JavaArchive, private val mappings: Map<String, String>) {
    
    private val remapper = CustomRemapper()
    
    fun applyMappings(): List<ClassWrapper> {
        val newClasses = ArrayList<ClassWrapper>()
        
        jar.classes.forEach { clazz ->
            val newClass = ClassWrapper(clazz.fileName)
            clazz.accept(ClassRemapper(newClass, remapper))
            newClass.fileName = "${newClass.name}.class"
            if (!newClass.sourceFile.isNullOrBlank())
                newClass.sourceFile = "${clazz.className}.java"
            newClass.sourceDebug = null
            newClasses.add(newClass)
        }
        
        return newClasses
    }
    
    private inner class CustomRemapper : SimpleRemapper(mappings) {
        override fun mapFieldName(owner: String, name: String, descriptor: String) =
            map("$owner.$name.$descriptor") ?: name
    }
    
}