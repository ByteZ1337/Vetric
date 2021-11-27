package xyz.xenondevs.vetric.supplier.impl

import xyz.xenondevs.vetric.supplier.StringSupplier
import kotlin.random.Random

val ALPHA_SUPPLIER by lazy { AlphaSupplier(1) }

private val COMBINING by lazy {
    intArrayOf(
        0x300, 0x301, 0x302, 0x303, 0x304,
        0x305, 0x306, 0x307, 0x308, 0x30A,
        0x30B, 0x30C, 0x30F, 0x313, 0x314,
        0x326, 0x327, 0x335, 0x336, 0x342,
        0x345
    ).map(Int::toChar)
}

private val CHAR_REGEX = Regex(".(?!\$)")

class CombiningSupplier(private val min: Int, private val max: Int) : StringSupplier("Combining") {
    
    /**
     * Generates a random string using the [ALPHA_SUPPLIER] and adds 10 random combining characters after each character.
     */
    override fun randomString(length: Int): String {
        return ALPHA_SUPPLIER
            .randomString(length)
            .replace(CHAR_REGEX, "$0" + COMBINING.shuffled().take(10).joinToString(""))
    }
    
    override fun randomString() = randomString(Random.nextInt(min, max + 1))
}