package xyz.xenondevs.obfuscator.utils

import org.objectweb.asm.Opcodes.*
import org.objectweb.asm.Type
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.MethodNode
import xyz.xenondevs.obfuscator.jvm.ClassPath
import xyz.xenondevs.obfuscator.jvm.ClassWrapper

object ASMUtils {
    
    /* ------------------ Constnats ------------------ */
    
    const val OBJECT_TYPE = "java/lang/Object"
    
    /* ------------------ Access Checks ------------------ */
    
    fun isPublic(access: Int) = access and ACC_PUBLIC != 0
    
    fun isPrivate(access: Int) = access and ACC_PRIVATE != 0
    
    fun isProtected(access: Int) = access and ACC_PROTECTED != 0
    
    fun isStatic(access: Int) = access and ACC_STATIC != 0
    
    fun isFinal(access: Int) = access and ACC_FINAL != 0
    
    fun isAbstract(access: Int) = access and ACC_ABSTRACT != 0
    
    fun isNative(access: Int) = access and ACC_NATIVE != 0
    
    fun isEnum(access: Int) = access and ACC_ENUM != 0
    
    fun isInterface(access: Int) = access and ACC_INTERFACE != 0
    
    /* ------------------ General Utilities ------------------ */
    
    fun buildMethodDesc(returnType: Type, params: List<Type>) =
        "(${params.joinToString("") { it.descriptor }})${returnType.descriptor}"
    
    fun getSuperClasses(wrapper: ClassWrapper, current: Set<String> = emptySet()): Set<String> {
        val parents = HashSet<String>()
        
        val superName = wrapper.superName
        if (!current.contains(superName)) {
            parents += superName
            if (OBJECT_TYPE != superName)
                parents += getSuperClasses(ClassPath.getClassWrapper(superName), parents)
        }
        
        wrapper.interfaces?.forEach {
            if (!current.contains(it)) {
                parents += it
                parents += getSuperClasses(ClassPath.getClassWrapper(it), parents)
            }
        }
        
        if (!current.contains(wrapper.name))
            parents += wrapper.name
        
        return parents
    }
    
    // TODO move to ClassWrapper
    fun isAssignableFrom(type1: String, type2: String): Boolean {
        if (OBJECT_TYPE == type1 || type1 == type2)
            return true
        
        val wrapper = ClassPath.getClassWrapper(type2)
        return getSuperClasses(wrapper).contains(type1)
    }
    
    // TODO Implement getSuperClasses
    fun getParent(method: MethodNode, clazz: ClassNode): ClassWrapper? {
        if (isStatic(method.access))
            return null
        
        if (clazz.superName != null) {
            val superClass = ClassPath.getClassWrapper(clazz.superName)
            val rec = getParent(method, superClass)
            if (hasMethod(method.name, method.desc, superClass) && rec == null)
                return superClass
            if (rec != null)
                return rec
        }
        
        if (clazz.interfaces != null) {
            clazz.interfaces.forEach { interf ->
                val interfaceClass = ClassPath.getClassWrapper(interf)
                val rec = getParent(method, interfaceClass)
                if (hasMethod(method.name, method.desc, interfaceClass) && rec == null)
                    return interfaceClass
                if (rec != null)
                    return rec
            }
        }
        return null
    }
    
    fun isInherited(method: MethodNode, clazz: ClassNode) =
        getParent(method, clazz) != null
    
    fun hasMethod(name: String, desc: String, clazz: ClassNode) =
        clazz.methods != null && clazz.methods.any { it.desc == desc && it.name == name }
    
}