package xyz.xenondevs.vetric.utils

import org.objectweb.asm.tree.AbstractInsnNode
import org.objectweb.asm.tree.LabelNode
import org.objectweb.asm.tree.LdcInsnNode
import org.objectweb.asm.util.Textifier
import org.objectweb.asm.util.TraceClassVisitor
import xyz.xenondevs.bytebase.jvm.ClassWrapper
import java.io.File
import java.io.PrintWriter
import java.io.StringWriter

/**
 * Gets all strings in the given class. (Does not include strings stored as field values yet)
 */
fun ClassWrapper.getStringPool(): Set<String> {
    return methods
        .asSequence()
        .flatMap { it.instructions }
        .filterTypeSub<LdcInsnNode, String> { it.cst }
        .map { it.cst as String }
        .toSet()
}

fun ClassWrapper.disassemble(): String {
    val writer = StringWriter()
    this.accept(TraceClassVisitor(null, Textifier(), PrintWriter(writer)))
    return writer.toString()
}

fun ClassWrapper.disassembleTo(file: File) {
    file.writeText(this.disassemble())
}

fun LabelNode.getIndex(): Int {
    var count = 0
    var current: AbstractInsnNode = this
    while (current.previous != null) {
        current = current.previous
        if (current is LabelNode)
            ++count
    }
    return count
}