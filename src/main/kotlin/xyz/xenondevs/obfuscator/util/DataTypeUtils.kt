package xyz.xenondevs.obfuscator.util

import java.io.Flushable
import java.lang.reflect.Modifier

fun UInt.toByteArray(): ByteArray {
    return byteArrayOf(
        (this shr 24).toByte(),
        (this shr 16).toByte(),
        (this shr 8).toByte(),
        this.toByte()
    )
}

@Suppress("UNCHECKED_CAST")
inline fun <reified T> Iterable<*>.filterTypeAnd(block: (T) -> Boolean): List<T> =
    filter { it is T && block(it) } as List<T>

fun <T> T.copyTo(obj: T) =
    this!!::class.java.declaredFields.forEach {
        if (!Modifier.isFinal(it.modifiers)) {
            it.isAccessible = true
            it.set(obj, it.get(this))
        }
    }

fun <T> T.flushClose() where T : Flushable, T : AutoCloseable {
    flush(); close()
}

fun IntRange.toIntArray(): IntArray {
    val size = this.last - this.first + 1
    var current = this.first - 1
    return IntArray(size) { ++current }
}

val Class<*>.internalName get() = canonicalName.replace('.', '/')