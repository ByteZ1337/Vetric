package xyz.xenondevs.obfuscator.util

inline fun <T> T.alsoIf(predicate: (T) -> Boolean, block: (T) -> Unit): T {
    if (predicate(this))
        block(this)
    return this
}

@Suppress("UNCHECKED_CAST")
inline fun <reified T> Iterable<*>.filterTypeAnd(block: (T) -> Boolean): List<T> =
        filter { it is T && block(it) } as List<T>

fun String.onlyContains(dir: String): Boolean =
        none { !dir.toCharArray().contains(it) }

fun String.onlyContainsIgnoreCase(dir: String): Boolean =
        toLowerCase().onlyContains(dir.toLowerCase())

fun String.reverseSubstring(start: Int, end: Int): String =
        substring(start, length - end)

fun String.reverseSubstring(amount: Int): String =
        substring(0, length - amount)

fun String.between(prefix: String, suffix: String): String =
        substring(indexOf(prefix) + prefix.length, lastIndexOf(suffix))
