package xyz.xenondevs.obfuscator.util

import java.security.SecureRandom
import kotlin.random.asKotlinRandom

object StringUtils {

    val RANDOM = SecureRandom().asKotlinRandom()

    val ALPHA_LOWER = ('a'..'z').joinToString("")
    val ALPHA_UPPER = ('A'..'Z').joinToString("")
    val NUMERIC = ('0'..'9').joinToString("")
    val ALPHA = ALPHA_LOWER + ALPHA_UPPER
    val ALPHA_NUMERIC = ALPHA + NUMERIC

    val generated = HashSet<String>()

    // Dictionary randoms

    fun randomString(length: Int, dict: String) =
        (1..length).map { dict.random() }.joinToString("")

    fun randomString(min: Int, max: Int, dict: String) =
        randomString((min..max).random(), dict)

    fun randomString(range: IntRange, dict: String) =
        randomString(range.random(), dict)

    // Actual randoms

    fun randomString(length: Int): String {
        val builder = StringBuilder()
        while (builder.length < length) {
            val codePoint = RANDOM.nextInt(Character.MAX_CODE_POINT)
            if (codePoint != '$'.toInt() && Character.isDefined(codePoint)
                && Character.isJavaIdentifierStart(codePoint) && Character.isJavaIdentifierPart(codePoint)
            ) {
                builder.appendCodePoint(codePoint)
            }
        }
        return builder.toString()
    }

    fun randomString(range: IntRange) =
        randomString(range.random())

    fun randomStringUnique(length: Int, set: HashSet<String> = generated): String {
        var random: String
        do random = randomString(length) while (set.contains(random))
        set += random
        return random
    }

    fun randomStringUnique(range: IntRange, set: HashSet<String> = generated) = randomStringUnique(range.random(), set)

    fun randomStringUnique(set: HashSet<String> = generated) = randomStringUnique(10..20, set)

    fun encrypt(text: String, key: String) = text.mapIndexed { index, c ->
        (c.toInt() xor key[index % key.length].toInt()).toChar()
    }.joinToString("")

}

fun String.between(start: Char, end: Char) = this.substringAfter(start).substringBeforeLast(end)
