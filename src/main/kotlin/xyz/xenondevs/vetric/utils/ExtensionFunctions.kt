package xyz.xenondevs.vetric.utils

import org.objectweb.asm.Opcodes.ACC_PRIVATE
import org.objectweb.asm.tree.FieldInsnNode
import org.objectweb.asm.tree.FieldNode
import org.objectweb.asm.tree.MethodInsnNode
import org.objectweb.asm.tree.MethodNode
import xyz.xenondevs.vetric.asm.Access
import xyz.xenondevs.vetric.jvm.ClassPath
import java.io.Flushable
import kotlin.reflect.KClass

fun Int.hasMask(mask: Int) = this and mask == mask

fun UInt.toByteArray() =
    byteArrayOf(
        (this shr 24).toByte(),
        (this shr 16).toByte(),
        (this shr 8).toByte(),
        this.toByte()
    )

@Suppress("UNCHECKED_CAST")
inline fun <reified T> Iterable<*>.filterTypeAnd(block: (T) -> Boolean) =
    filter { it is T && block(it) } as List<T>

@Suppress("UNCHECKED_CAST")
inline fun <reified T> Array<*>.filterTypeAnd(block: (T) -> Boolean) =
    filter { it is T && block(it) } as List<T>

fun <T> T.flushClose() where T : Flushable, T : AutoCloseable {
    flush(); close()
}

fun IntRange.toIntArray(): IntArray {
    val size = this.last - this.first + 1
    var current = this.first - 1
    return IntArray(size) { ++current }
}

val Class<*>.internalName get() = name.replace('.', '/')

val KClass<*>.internalName get() = java.internalName

val FieldNode.accessWrapper get() = Access(access)

val FieldInsnNode.ownerWrapper
    get() = ClassPath.getClassWrapper(owner)

val FieldInsnNode.node
    get() = ownerWrapper.getField(name, desc)

val FieldInsnNode.access
    get() = node?.accessWrapper ?: Access(ACC_PRIVATE)

val MethodNode.accessWrapper get() = Access(access)

val MethodInsnNode.ownerWrapper
    get() = ClassPath.getClassWrapper(owner)

val MethodInsnNode.node
    get() = ownerWrapper.getMethod(name, desc)

val MethodInsnNode.access
    get() = node?.accessWrapper ?: Access(ACC_PRIVATE)
