package xyz.xenondevs.vetric.util

object StringUtils {
    
    fun encrypt(text: String, key: String) = text.mapIndexed { index, c ->
        (c.code xor key[index % key.length].code).toChar()
    }.joinToString("")
    
}

fun String.capitalize() = this.replaceFirstChar(Char::titlecaseChar)