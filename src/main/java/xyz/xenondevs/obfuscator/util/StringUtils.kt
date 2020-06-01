package xyz.xenondevs.obfuscator.util

import xyz.xenondevs.obfuscator.util.MathUtils.randomInt

object StringUtils {
    val ALPHA_LOWER = ('a'..'z').joinToString("")
    val ALPHA_UPPER = ('A'..'Z').joinToString("")
    val NUMERIC = ('0'..'9').joinToString("")
    val ALPHA = ALPHA_LOWER + ALPHA_UPPER
    val ALPHA_NUMERIC = ALPHA + NUMERIC

    fun randomString(len: Int, dir: String): String =
            (1..len).map { dir.random() }.joinToString("")

    fun randomString(min: Int, max: Int, dir: String): String =
            randomString(randomInt(min..max), dir)

    fun randomString(range: IntRange, dir: String): String =
            randomString(randomInt(range), dir)

    fun String.onlyContains(dir: String): Boolean =
            this.none { !dir.toCharArray().contains(it) }

    fun String.onlyContainsIgnoreCase(dir: String): Boolean =
            this.toLowerCase().onlyContains(dir.toLowerCase())

    fun String.containsIgnoreCase(other: String): Boolean =
            this.toLowerCase().contains(other.toLowerCase())

    fun String.reverseSubstring(start: Int, end: Int): String =
            this.substring(start, this.length - end)

    fun String.reverseSubstring(amount: Int): String =
            this.substring(0, this.length - amount)
}


