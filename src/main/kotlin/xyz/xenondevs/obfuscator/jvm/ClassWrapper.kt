package xyz.xenondevs.obfuscator.jvm

import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassReader.SKIP_FRAMES
import org.objectweb.asm.Opcodes.ASM9
import org.objectweb.asm.tree.ClassNode
import xyz.xenondevs.obfuscator.asm.ExternalClassWriter
import xyz.xenondevs.obfuscator.utils.ASMUtils

class ClassWrapper(var fileName: String) : ClassNode(ASM9) {

    val originalName = fileName

    val inheritanceTree
        get() = ClassPath.getTree(this)
    val parentClasses
        get() = inheritanceTree.parentClasses
    val subClasses
        get() = inheritanceTree.subClasses

    constructor(fileName: String, byteCode: ByteArray) : this(fileName) {
        ClassReader(byteCode).accept(this, SKIP_FRAMES)
    }

    fun getFullSubClasses(): HashSet<String> {
        val fullSubClasses = HashSet<String>()
        subClasses.forEach {
            fullSubClasses += it
            fullSubClasses += ClassPath.getClassWrapper(it).getFullSubClasses()
        }
        return fullSubClasses
    }

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