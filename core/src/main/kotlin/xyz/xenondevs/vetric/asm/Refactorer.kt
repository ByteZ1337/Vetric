package xyz.xenondevs.vetric.asm

import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.Label
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.commons.ClassRemapper
import org.objectweb.asm.commons.SimpleRemapper
import xyz.xenondevs.vetric.jvm.ClassWrapper
import xyz.xenondevs.vetric.jvm.JavaArchive

class Refactorer(private val jar: JavaArchive, private val mappings: Map<String, String>) {
    
    private val memberRemapper = MemberRemapper()
    
    fun applyMappings(): List<ClassWrapper> {
        val newClasses = ArrayList<ClassWrapper>()
        
        jar.classes.forEach { clazz ->
            val newClass = ClassWrapper(clazz.fileName)
            clazz.accept(CustomClassRemapper(newClass))
            newClass.fileName = "${newClass.name}.class"
            if (!newClass.sourceFile.isNullOrBlank())
                newClass.sourceFile = "${clazz.className}.java"
            newClass.sourceDebug = null
            newClasses.add(newClass)
        }
        
        return newClasses
    }
    
    private inner class MemberRemapper : SimpleRemapper(mappings) {
        
        override fun mapFieldName(owner: String, name: String, descriptor: String) =
            map("$owner.$name.$descriptor") ?: name
        
        fun mapLocalVariableName(owner: String, methodName: String, methodDesc: String, varName: String, varDesc: String) =
            map("$owner.$methodName$methodDesc.$varName.$varDesc") ?: varName
    }
    
    private inner class CustomClassRemapper(classVisitor: ClassVisitor) : ClassRemapper(classVisitor, memberRemapper) {
        
        override fun visitMethod(access: Int, name: String, descriptor: String, signature: String?, exceptions: Array<out String>?): MethodVisitor? {
            val basicRemapper = super.visitMethod(access, name, descriptor, signature, exceptions)
            return if (basicRemapper == null) null else CustomMethodRemapper(basicRemapper, className, descriptor, name)
        }
        
    }
    
    private inner class CustomMethodRemapper(
        methodVisitor: MethodVisitor,
        val owner: String,
        val desc: String,
        val name: String
    ) : MethodVisitor(Opcodes.ASM9, methodVisitor) {
        override fun visitLocalVariable(name: String, desc: String, signature: String?, start: Label, end: Label, index: Int) {
            super.visitLocalVariable(
                memberRemapper.mapLocalVariableName(owner, this.name, this.desc, name, desc),
                desc, signature, start, end, index
            )
        }
    }
    
}