package xyz.xenondevs.obfuscator.util

import java.security.SecureRandom
import kotlin.random.asKotlinRandom

object MathUtils {

    val random = SecureRandom().asKotlinRandom()

    fun randomInt(min: Int, max: Int): Int {
        return if (max < min) max else random.nextInt(min, max)
    }

    fun randomInt(range: IntRange): Int = randomInt(range.first, range.last)

    fun randomInt(): Int = random.nextInt()

}