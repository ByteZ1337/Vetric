package xyz.xenondevs.vetric.utils

import org.objectweb.asm.Opcodes.ACC_PRIVATE
import org.objectweb.asm.Type
import org.objectweb.asm.Type.*
import org.objectweb.asm.tree.*
import xyz.xenondevs.vetric.asm.access.ReferencingAccess
import xyz.xenondevs.vetric.asm.access.ValueAccess
import xyz.xenondevs.vetric.jvm.ClassPath
import java.io.Flushable
import kotlin.reflect.KClass

fun String.between(start: Char, end: Char) = this.substringAfterLast(start).substringBeforeLast(end)

fun String.startsWithAny(vararg prefixes: Char) = prefixes.any(this::startsWith)

fun String.endsWithAny(vararg prefixes: Char) = prefixes.any(this::endsWith)

fun Int.hasMask(mask: Int) = this and mask == mask

fun Int.setMask(mask: Int, value: Boolean) = if (value) this or mask else this and mask.inv()

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

fun InsnList.remove(vararg insn: AbstractInsnNode) = insn.forEach(this::remove)

fun InsnList.replace(insn: AbstractInsnNode, replacement: AbstractInsnNode) {
    insertBefore(insn, replacement)
    remove(insn)
}

fun InsnList.replace(insn: AbstractInsnNode, replacement: InsnList) {
    insertBefore(insn, replacement)
    remove(insn)
}

val Class<*>.internalName get() = name.replace('.', '/')

val KClass<*>.internalName get() = java.internalName

fun ClassNode.hasAnnotations() = !this.invisibleAnnotations.isNullOrEmpty() || !this.visibleAnnotations.isNullOrEmpty()

val Type.name: String
    get() = when (sort) {
        OBJECT -> internalName
        ARRAY -> elementType.name
        METHOD -> returnType.name
        INT -> "java/lang/Integer"
        CHAR -> "java/lang/Character"
        else -> "java/lang/" + className.capitalize()
    }

val Type.clazz
    get() = ClassPath.getClassWrapper(name)

val FieldNode.accessWrapper get() = ReferencingAccess({ this.access }, { this.access = it })

fun FieldNode.hasAnnotations() = !this.invisibleAnnotations.isNullOrEmpty() || !this.visibleAnnotations.isNullOrEmpty()

val FieldInsnNode.ownerWrapper
    get() = ClassPath.getClassWrapper(owner)

val FieldInsnNode.node
    get() = ownerWrapper.getField(name, desc)

val FieldInsnNode.access
    get() = node?.accessWrapper ?: ValueAccess(ACC_PRIVATE)

val MethodNode.accessWrapper get() = ReferencingAccess({ this.access }, { this.access = it })

fun MethodNode.hasAnnotations() = !this.visibleAnnotations.isNullOrEmpty() || !this.invisibleAnnotations.isNullOrEmpty()

val MethodInsnNode.ownerWrapper
    get() = ClassPath.getClassWrapper(owner)

val MethodInsnNode.node
    get() = ownerWrapper.getMethod(name, desc)

val MethodInsnNode.access
    get() = node?.accessWrapper ?: ValueAccess(ACC_PRIVATE)
