package xyz.xenondevs.obfuscator.suppliers

import xyz.xenondevs.obfuscator.utils.toIntArray
import kotlin.random.Random

open class CharSupplier(name: String, val max: Int, val min: Int, val chars: List<Char>) : StringSupplier(name) {
    
    constructor(name: String, defaultLength: Int = 20, chars: List<Char>) : this(name, defaultLength, defaultLength, chars)
    
    override fun randomString(length: Int) = (1..length).joinToString("") { chars.random().toString() }
    
    override fun randomString() = randomString(Random.nextInt(min, max + 1))
}

class AlphaSupplier(min: Int, max: Int) : CharSupplier("Alpha", min, max, ('a'..'z') + ('A'..'Z')) {
    constructor(defaultLength: Int = 20) : this(defaultLength, defaultLength)
}

class AlphaNumericSupplier(min: Int, max: Int) : CharSupplier("AlphaNumeric", min, max, ('a'..'z') + ('A'..'Z') + ('0'..'9')) {
    constructor(defaultLength: Int = 20) : this(defaultLength, defaultLength)
}

class InvisibleSupplier(min: Int, max: Int) : CharSupplier("Invisible", min, max, (0x2000..0x200F).map(Int::toChar)) {
    constructor(defaultLength: Int = 20) : this(defaultLength, defaultLength)
}

class DotsSupplier(min: Int, max: Int) : CharSupplier(
    "Dots", min, max, intArrayOf(*(0x2cc..0x355).toIntArray(), 0x10a788, 0x10abec).map(Int::toChar)
) {
    constructor(defaultLength: Int = 20) : this(defaultLength, defaultLength)
}
