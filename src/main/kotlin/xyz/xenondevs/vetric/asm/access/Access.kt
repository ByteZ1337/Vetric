package xyz.xenondevs.vetric.asm.access

interface Access {
    
    fun isPublic(): Boolean
    
    fun isPrivate(): Boolean
    
    fun isProtected(): Boolean
    
    fun isStatic(): Boolean
    
    fun isFinal(): Boolean
    
    fun isSuper(): Boolean
    
    fun isSynchronized(): Boolean
    
    fun isOpen(): Boolean
    
    fun isTransitive(): Boolean
    
    fun isVolatile(): Boolean
    
    fun isBridge(): Boolean
    
    fun isStaticPhase(): Boolean
    
    fun isVarargs(): Boolean
    
    fun isTransient(): Boolean
    
    fun isNative(): Boolean
    
    fun isInterface(): Boolean
    
    fun isAbstract(): Boolean
    
    fun isStrict(): Boolean
    
    fun isSynthetic(): Boolean
    
    fun isAnnotation(): Boolean
    
    fun isEnum(): Boolean
    
    fun isMandated(): Boolean
    
    fun isModule(): Boolean
    
    fun hasFlags(vararg flags: Int): Boolean
    
    fun none(vararg flags: Int): Boolean
    
}