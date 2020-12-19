package xyz.xenondevs.vetric.jvm

import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassReader.SKIP_FRAMES
import org.objectweb.asm.Opcodes.ASM9
import org.objectweb.asm.Type
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.MethodNode
import xyz.xenondevs.vetric.asm.Access
import xyz.xenondevs.vetric.asm.CustomClassWriter

class ClassWrapper(var fileName: String) : ClassNode(ASM9) {
    
    val originalName = fileName
    
    val inheritanceTree
        get() = ClassPath.getTree(this)
    val parentClasses
        get() = inheritanceTree.parentClasses
    val subClasses
        get() = inheritanceTree.subClasses
    val className
        get() = name.substringAfter('/')
    val accessWrapper
        get() = Access(access)
    val type: Type
        get() = Type.getType("L$name;")
    
    constructor(fileName: String, byteCode: ByteArray) : this(fileName) {
        ClassReader(byteCode).accept(this, SKIP_FRAMES)
    }
    
    fun getFullSubClasses(): HashSet<String> =
        HashSet(subClasses.map(ClassPath::getClassWrapper).flatMap { it.getFullSubClasses() + it.name })
    
    operator fun contains(method: MethodNode) =
        methods.any { method.name == it.name && method.desc == it.desc }
    
    fun getByteCode() = CustomClassWriter().also(this::accept).toByteArray()!!
    
    fun isInterface() = accessWrapper.isInterface()
    
    fun isEnum() = accessWrapper.isEnum()
    
    override fun hashCode() = name.hashCode() xor fileName.hashCode()
    
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        
        other as ClassWrapper
        
        return name == other.name && fileName == other.fileName
    }
    
}
