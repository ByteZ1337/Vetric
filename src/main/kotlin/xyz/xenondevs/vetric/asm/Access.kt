package xyz.xenondevs.vetric.asm

import org.objectweb.asm.Opcodes.*
import xyz.xenondevs.vetric.utils.hasMask

class Access(val access: Int) {
    
    fun isPublic() = access.hasMask(ACC_PUBLIC)
    
    fun isPrivate() = access.hasMask(ACC_PRIVATE)
    
    fun isProtected() = access.hasMask(ACC_PROTECTED)
    
    fun isStatic() = access.hasMask(ACC_STATIC)
    
    fun isFinal() = access.hasMask(ACC_FINAL)
    
    fun isSuper() = access.hasMask(ACC_SUPER)
    
    fun isSynchronized() = access.hasMask(ACC_SYNCHRONIZED)
    
    fun isOpen() = access.hasMask(ACC_OPEN)
    
    fun isTransitive() = access.hasMask(ACC_TRANSITIVE)
    
    fun isVolatile() = access.hasMask(ACC_VOLATILE)
    
    fun isBridge() = access.hasMask(ACC_BRIDGE)
    
    fun isStaticPhase() = access.hasMask(ACC_STATIC_PHASE)
    
    fun isVarargs() = access.hasMask(ACC_VARARGS)
    
    fun isTransient() = access.hasMask(ACC_TRANSIENT)
    
    fun isNative() = access.hasMask(ACC_NATIVE)
    
    fun isInterface() = access.hasMask(ACC_INTERFACE)
    
    fun isAbstract() = access.hasMask(ACC_ABSTRACT)
    
    fun isStrict() = access.hasMask(ACC_STRICT)
    
    fun isSynthetic() = access.hasMask(ACC_SYNTHETIC)
    
    fun isAnnotation() = access.hasMask(ACC_ANNOTATION)
    
    fun isEnum() = access.hasMask(ACC_ENUM)
    
    fun isMandated() = access.hasMask(ACC_MANDATED)
    
    fun isModule() = access.hasMask(ACC_MODULE)
    
    fun hasFlags(vararg flags: Int): Boolean {
        val mask = flags.reduce { i1, i2 -> i1 or i2 }
        return access.hasMask(mask)
    }
    
    fun none(vararg flags: Int) = flags.none(access::hasMask)
    
    fun isPublicClass() = !isEnum() && !isInterface() && isPublic()
}