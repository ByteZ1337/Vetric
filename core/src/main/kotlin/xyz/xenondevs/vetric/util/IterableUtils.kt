package xyz.xenondevs.vetric.util

import java.io.Flushable

@Suppress("UNCHECKED_CAST")
inline fun <reified T> Iterable<*>.filterTypeAnd(block: (T) -> Boolean) =
    filter { it is T && block(it) } as List<T>

@Suppress("UNCHECKED_CAST")
inline fun <reified T> Array<*>.filterTypeAnd(block: (T) -> Boolean) =
    filter { it is T && block(it) } as List<T>


fun IntRange.toIntArray(): IntArray {
    val size = this.last - this.first + 1
    var current = this.first - 1
    return IntArray(size) { ++current }
}