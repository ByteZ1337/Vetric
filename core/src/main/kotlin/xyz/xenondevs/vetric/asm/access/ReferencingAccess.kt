package xyz.xenondevs.vetric.asm.access

import org.objectweb.asm.Opcodes.*
import xyz.xenondevs.vetric.util.hasMask
import xyz.xenondevs.vetric.util.setMask

class ReferencingAccess(val get: () -> Int, val set: (Int) -> Unit) : Access {
    
    override fun isPublic() = get().hasMask(ACC_PUBLIC)
    
    fun setPublic(value: Boolean = true) = set(get().setMask(ACC_PUBLIC, value))
    
    override fun isPrivate() = get().hasMask(ACC_PRIVATE)
    
    fun setPrivate(value: Boolean = true) = set(get().setMask(ACC_PRIVATE, value))
    
    override fun isProtected() = get().hasMask(ACC_PROTECTED)
    
    fun setProtected(value: Boolean = true) = set(get().setMask(ACC_PROTECTED, value))
    
    override fun isStatic() = get().hasMask(ACC_STATIC)
    
    fun setStatic(value: Boolean = true) = set(get().setMask(ACC_STATIC, value))
    
    override fun isFinal() = get().hasMask(ACC_FINAL)
    
    fun setFinal(value: Boolean = true) = set(get().setMask(ACC_FINAL, value))
    
    override fun isSuper() = get().hasMask(ACC_SUPER)
    
    fun setSuper(value: Boolean = true) = set(get().setMask(ACC_SUPER, value))
    
    override fun isSynchronized() = get().hasMask(ACC_SYNCHRONIZED)
    
    fun setSynchronized(value: Boolean = true) = set(get().setMask(ACC_SYNCHRONIZED, value))
    
    override fun isOpen() = get().hasMask(ACC_OPEN)
    
    fun setOpen(value: Boolean = true) = set(get().setMask(ACC_OPEN, value))
    
    override fun isTransitive() = get().hasMask(ACC_TRANSITIVE)
    
    fun setTransitive(value: Boolean = true) = set(get().setMask(ACC_TRANSITIVE, value))
    
    override fun isVolatile() = get().hasMask(ACC_VOLATILE)
    
    fun setVolatile(value: Boolean = true) = set(get().setMask(ACC_VOLATILE, value))
    
    override fun isBridge() = get().hasMask(ACC_BRIDGE)
    
    fun setBridge(value: Boolean = true) = set(get().setMask(ACC_BRIDGE, value))
    
    override fun isStaticPhase() = get().hasMask(ACC_STATIC_PHASE)
    
    fun setStaticPhase(value: Boolean = true) = set(get().setMask(ACC_STATIC_PHASE, value))
    
    override fun isVarargs() = get().hasMask(ACC_VARARGS)
    
    fun setVarargs(value: Boolean = true) = set(get().setMask(ACC_VARARGS, value))
    
    override fun isTransient() = get().hasMask(ACC_TRANSIENT)
    
    fun setTransient(value: Boolean = true) = set(get().setMask(ACC_TRANSIENT, value))
    
    override fun isNative() = get().hasMask(ACC_NATIVE)
    
    fun setNative(value: Boolean = true) = set(get().setMask(ACC_NATIVE, value))
    
    override fun isInterface() = get().hasMask(ACC_INTERFACE)
    
    fun setInterface(value: Boolean = true) = set(get().setMask(ACC_INTERFACE, value))
    
    override fun isAbstract() = get().hasMask(ACC_ABSTRACT)
    
    fun setAbstract(value: Boolean = true) = set(get().setMask(ACC_ABSTRACT, value))
    
    override fun isStrict() = get().hasMask(ACC_STRICT)
    
    fun setStrict(value: Boolean = true) = set(get().setMask(ACC_STRICT, value))
    
    override fun isSynthetic() = get().hasMask(ACC_SYNTHETIC)
    
    fun setSynthetic(value: Boolean = true) = set(get().setMask(ACC_SYNTHETIC, value))
    
    override fun isAnnotation() = get().hasMask(ACC_ANNOTATION)
    
    fun setAnnotation(value: Boolean = true) = set(get().setMask(ACC_ANNOTATION, value))
    
    override fun isEnum() = get().hasMask(ACC_ENUM)
    
    fun setEnum(value: Boolean = true) = set(get().setMask(ACC_ENUM, value))
    
    override fun isMandated() = get().hasMask(ACC_MANDATED)
    
    fun setMandated(value: Boolean = true) = set(get().setMask(ACC_MANDATED, value))
    
    override fun isModule() = get().hasMask(ACC_MODULE)
    
    fun setModule(value: Boolean = true) = set(get().setMask(ACC_MODULE, value))
    
    override fun hasFlags(vararg flags: Int): Boolean {
        val mask = flags.reduce { i1, i2 -> i1 or i2 }
        return get().hasMask(mask)
    }
    
    fun setFlags(vararg flags: Int, value: Boolean = true) {
        val mask = flags.reduce { i1, i2 -> i1 or i2 }
        return set(get().setMask(mask, value))
    }
    
    override fun none(vararg flags: Int): Boolean {
        val access = get()
        return flags.none(access::hasMask)
    }
    
    fun isPublicClass() = !isEnum() && !isInterface() && isPublic()
    
}