package xyz.xenondevs.vetric.utils

import org.objectweb.asm.tree.LdcInsnNode
import xyz.xenondevs.bytebase.jvm.ClassWrapper

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