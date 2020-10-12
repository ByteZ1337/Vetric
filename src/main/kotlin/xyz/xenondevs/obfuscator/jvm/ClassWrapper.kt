package xyz.xenondevs.obfuscator.jvm

import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassReader.EXPAND_FRAMES
import org.objectweb.asm.Opcodes.ASM9
import org.objectweb.asm.tree.ClassNode
import xyz.xenondevs.obfuscator.asm.ExternalClassWriter
import xyz.xenondevs.obfuscator.util.ASMUtils

class ClassWrapper(var fileName: String, val jar: JavaArchive? = null) : ClassNode(ASM9) {

    val originalName = fileName

    val inheritanceTree
        get() = ClassPath.getTree(this)

    constructor(fileName: String, byteCode: ByteArray, jar: JavaArchive? = null) : this(fileName, jar) {
        ClassReader(byteCode).accept(this, EXPAND_FRAMES)
    }

    fun getSubClasses() = inheritanceTree.subClasses

    fun getFullSubClasses(): HashSet<String> {
        val subClasses = HashSet<String>()
        getSubClasses().forEach {
            subClasses += it
            subClasses += ClassPath.getClassWrapper(it).getFullSubClasses()
        }
        return subClasses
    }

    fun getParentClasses() = inheritanceTree.parentClasses

    fun getByteCode() = ExternalClassWriter().also { accept(it) }.toByteArray()!!

    fun isInterface() = ASMUtils.isInterface(access)

    fun isEnum() = ASMUtils.isEnum(access)

    override fun hashCode() = name.hashCode() xor fileName.hashCode()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ClassWrapper

        return name == other.name && fileName == other.fileName
    }

}
