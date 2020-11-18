package xyz.xenondevs.obfuscator.suppliers

import xyz.xenondevs.obfuscator.utils.toIntArray

open class CharSupplier(name: String, val defaultLength: Int = 20, val chars: List<Char>) : StringSupplier(name) {
    
    override fun randomString(length: Int) = (1..length).joinToString("") { chars.random().toString() }
    
    fun randomString() = randomString(defaultLength)
}

class AlphaSupplier(defaultLength: Int) : CharSupplier("Alpha", defaultLength, ('a'..'z') + ('A'..'Z'))

class AlphaNumericSupplier(defaultLength: Int) : CharSupplier("AlphaNumeric", defaultLength, ('a'..'z') + ('A'..'Z') + ('0'..'9'))

class InvisibleSupplier(defaultLength: Int) : CharSupplier("Invisible", defaultLength, (0x2000..0x200F).map(Int::toChar))

class DotsSupplier(defaultLength: Int) : CharSupplier(
    "Dots", defaultLength, intArrayOf(*(0x2cc..0x355).toIntArray(), 0x10a788, 0x10abec).map(Int::toChar)
)
