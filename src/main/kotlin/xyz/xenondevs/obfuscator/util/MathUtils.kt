package xyz.xenondevs.obfuscator.util

import java.security.SecureRandom
import kotlin.random.asKotlinRandom

object MathUtils {

    val random = SecureRandom().asKotlinRandom()

    fun randomInt(min: Int, max: Int) =
            if (max < min) max else random.nextInt(min, max)

    fun randomInt(range: IntRange) = randomInt(range.first, range.last)

    fun randomInt() = random.nextInt()

}