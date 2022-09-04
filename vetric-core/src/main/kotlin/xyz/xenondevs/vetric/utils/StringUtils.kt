package xyz.xenondevs.vetric.utils

fun String.pluralize(count: Int): String {
    return if (count == 1) this else this + "s"
}

fun String.section(start: Char, end: Char): String? {
    val startIndex = indexOf(start)
    val endIndex = lastIndexOf(end) + 1
    return if (startIndex == -1 || endIndex == -1) null else substring(startIndex, endIndex)
}