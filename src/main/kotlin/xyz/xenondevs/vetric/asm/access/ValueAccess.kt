package xyz.xenondevs.vetric.asm.access

import org.objectweb.asm.Opcodes.*
import xyz.xenondevs.vetric.utils.hasMask

class ValueAccess(val access: Int) : Access {
    
    override fun isPublic() = access.hasMask(ACC_PUBLIC)
    
    override fun isPrivate() = access.hasMask(ACC_PRIVATE)
    
    override fun isProtected() = access.hasMask(ACC_PROTECTED)
    
    override fun isStatic() = access.hasMask(ACC_STATIC)
    
    override fun isFinal() = access.hasMask(ACC_FINAL)
    
    override fun isSuper() = access.hasMask(ACC_SUPER)
    
    override fun isSynchronized() = access.hasMask(ACC_SYNCHRONIZED)
    
    override fun isOpen() = access.hasMask(ACC_OPEN)
    
    override fun isTransitive() = access.hasMask(ACC_TRANSITIVE)
    
    override fun isVolatile() = access.hasMask(ACC_VOLATILE)
    
    override fun isBridge() = access.hasMask(ACC_BRIDGE)
    
    override fun isStaticPhase() = access.hasMask(ACC_STATIC_PHASE)
    
    override fun isVarargs() = access.hasMask(ACC_VARARGS)
    
    override fun isTransient() = access.hasMask(ACC_TRANSIENT)
    
    override fun isNative() = access.hasMask(ACC_NATIVE)
    
    override fun isInterface() = access.hasMask(ACC_INTERFACE)
    
    override fun isAbstract() = access.hasMask(ACC_ABSTRACT)
    
    override fun isStrict() = access.hasMask(ACC_STRICT)
    
    override fun isSynthetic() = access.hasMask(ACC_SYNTHETIC)
    
    override fun isAnnotation() = access.hasMask(ACC_ANNOTATION)
    
    override fun isEnum() = access.hasMask(ACC_ENUM)
    
    override fun isMandated() = access.hasMask(ACC_MANDATED)
    
    override fun isModule() = access.hasMask(ACC_MODULE)
    
    override fun hasFlags(vararg flags: Int): Boolean {
        val mask = flags.reduce { i1, i2 -> i1 or i2 }
        return access.hasMask(mask)
    }
    
    override fun none(vararg flags: Int) = flags.none(access::hasMask)
    
    fun isPublicClass() = !isEnum() && !isInterface() && isPublic()
}