package xyz.xenondevs.vetric.util

fun String.capitalize() = this.replaceFirstChar(Char::titlecaseChar)

fun String.between(start: Char, end: Char) = this.substringAfterLast(start).substringBeforeLast(end)

fun String.startsWithAny(vararg prefixes: Char) = prefixes.any(this::startsWith)

fun String.endsWithAny(vararg prefixes: Char) = prefixes.any(this::endsWith)

object StringUtils {
    
    fun encrypt(text: String, key: String) = text.mapIndexed { index, c ->
        (c.code xor key[index % key.length].code).toChar()
    }.joinToString("")
}
