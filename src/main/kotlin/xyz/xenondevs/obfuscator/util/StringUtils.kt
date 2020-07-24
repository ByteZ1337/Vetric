package xyz.xenondevs.obfuscator.util

import xyz.xenondevs.obfuscator.util.MathUtils.randomInt

object StringUtils {
    val ALPHA_LOWER = ('a'..'z').joinToString("")
    val ALPHA_UPPER = ('A'..'Z').joinToString("")
    val NUMERIC = ('0'..'9').joinToString("")
    val ALPHA = ALPHA_LOWER + ALPHA_UPPER
    val ALPHA_NUMERIC = ALPHA + NUMERIC

    val generated = HashSet<String>()

    fun randomString(length: Int, dir: String) =
            (1..length).map { dir.random() }.joinToString("")

    fun randomString(min: Int, max: Int, dir: String) =
            randomString(randomInt(min..max), dir)

    fun randomString(range: IntRange, dir: String) =
            randomString(randomInt(range), dir)

    fun randomString(length: Int): String {
        val out = StringBuffer()
        while (out.length < length) {
            val rdm = randomInt() % /*'z'.toInt()*/Character.MAX_CODE_POINT
            if (rdm != '$'.toInt() && Character.isDefined(rdm) && Character.isJavaIdentifierPart(rdm) && Character.isJavaIdentifierStart(rdm))
                out.appendCodePoint(rdm)
        }
        return out.toString()
    }

    fun randomString(range: IntRange) =
            randomString(randomInt(range))

    fun randomStringUnique(range: IntRange): String {
        var random: String
        do {
            random = randomString(range)
        } while (generated.contains(random))
        generated.add(random)
        return random
    }

    fun randomStringUnique() = randomString(10..20)

}

