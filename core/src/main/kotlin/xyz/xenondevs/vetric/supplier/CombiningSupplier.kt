package xyz.xenondevs.vetric.supplier

import kotlin.random.Random

// Breaks RSyntaxTextArea font rendering used by lots of decompilers
// WARNING: This Supplier skyrockets the file size! A single field name can take up to 5kb!
class CombiningSupplier(private val min: Int, private val max: Int) : StringSupplier("Combining") {
    
    constructor(defaultLength: Int = 5) : this(defaultLength, defaultLength)
    
    private companion object {
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
    }
    
    override fun randomString(length: Int) =
        ALPHA_SUPPLIER.randomString(length).map {
            it + COMBINING.shuffled().take(10).joinToString("")
        }.joinToString("")
    
    override fun randomString() = randomString(Random.nextInt(min, max + 1))
}