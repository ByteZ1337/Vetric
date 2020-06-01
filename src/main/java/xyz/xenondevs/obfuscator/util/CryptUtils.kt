package xyz.xenondevs.obfuscator.util

import kotlin.experimental.xor

object CryptUtils {

    fun encrypt(text: String, key: String): String =
            text.mapIndexed { index, c -> (c.toByte() xor key[index % key.length].toByte()).toChar() }.joinToString("")

}