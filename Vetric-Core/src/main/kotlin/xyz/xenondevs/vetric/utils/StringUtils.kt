package xyz.xenondevs.vetric.utils

fun String.pluralize(count: Int): String {
    return if (count == 1) this else this + "s"
}