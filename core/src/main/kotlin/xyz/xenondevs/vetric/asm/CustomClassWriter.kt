package xyz.xenondevs.vetric.asm

import org.objectweb.asm.ClassWriter
import xyz.xenondevs.vetric.jvm.ClassPath
import xyz.xenondevs.vetric.util.asm.ASMUtils
import xyz.xenondevs.vetric.util.asm.ASMUtils.OBJECT_TYPE

/**
 * ASM insists on having some classes loaded in the current classpath... So we bypass it
 *
 * Concept by ItzSomebody
 */
class CustomClassWriter(flags: Int = COMPUTE_FRAMES) : ClassWriter(flags) {
    
    override fun getCommonSuperClass(type1: String, type2: String): String {
        if (OBJECT_TYPE == type1 || OBJECT_TYPE == type2)
            return OBJECT_TYPE
        
        val first: String = findCommonSuperName(type1, type2)
        val second: String = findCommonSuperName(type2, type1)
        
        if (OBJECT_TYPE != first)
            return first
        if (OBJECT_TYPE != second)
            return second
        
        return getCommonSuperClass(ClassPath.getClassWrapper(type1).superName, ClassPath.getClassWrapper(type2).superName)
    }
    
    private fun findCommonSuperName(type1: String, type2: String): String {
        var first = ClassPath.getClassWrapper(type1)
        val second = ClassPath.getClassWrapper(type2)
        
        if (ASMUtils.isAssignableFrom(type1, type2))
            return type1
        else if (ASMUtils.isAssignableFrom(type2, type1))
            return type2
        
        if (first.isInterface() || second.isInterface())
            return OBJECT_TYPE
        
        var new: String
        do {
            new = first.superName
            first = ClassPath.getClassWrapper(new)
        } while (!ASMUtils.isAssignableFrom(new, type2))
        
        return new
    }
    
}