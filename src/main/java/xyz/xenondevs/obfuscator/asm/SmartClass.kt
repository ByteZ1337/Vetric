package xyz.xenondevs.obfuscator.asm

import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassReader.EXPAND_FRAMES
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.FieldNode
import org.objectweb.asm.tree.MethodNode


@ExperimentalStdlibApi
class SmartClass(var fileName: String, byteCode: ByteArray, val jar: SmartJar) {

    val originalName = fileName

    var node: ClassNode = ClassNode()

    fun getMethods(): MutableList<MethodNode>? = node.methods

    fun getFields(): MutableList<FieldNode>? = node.fields

    init {
        val reader = ClassReader(byteCode)
        reader.accept(node, EXPAND_FRAMES)
    }

    fun getClassName(): String = fileName.subSequence(fileName.lastIndexOf('/') + 1, fileName.length - 6).toString()

    fun getFullPath(): String = node.name

    fun addMethod(methodNode: MethodNode) {
        if (node.methods == null)
            node.methods = ArrayList()
        node.methods.add(methodNode)
    }

    fun toByteCode(): ByteArray {
        val cw = ClassWriter(ClassWriter.COMPUTE_MAXS)
        node.accept(cw)
        return cw.toByteArray()
    }

    fun update() {
        jar.files.remove(originalName)
        jar.files.remove(fileName)
        jar.files[fileName] = toByteCode()
    }

}