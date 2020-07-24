package xyz.xenondevs.obfuscator.util

object CryptUtils {

    fun encrypt(text: String, key: String): String =
            text.mapIndexed { index, c -> (c.toInt() xor key[index % key.length].toInt()).toChar() }.joinToString("")

}