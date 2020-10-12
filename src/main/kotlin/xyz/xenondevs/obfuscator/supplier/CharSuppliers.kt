package xyz.xenondevs.obfuscator.supplier

import xyz.xenondevs.obfuscator.util.toIntArray

open class CharSupplier(name: String, val chars: List<Char>) : StringSupplier(name) {
    override fun randomString(length: Int) = (1 until length).joinToString("") { chars.random().toString() }
}

object AlphaSupplier : CharSupplier("Alpha", ('A'..'z').filter(Char::isLetter))

object AlphaNumericSupplier : CharSupplier("AlphaNumeric", ('0'..'z').filter(Char::isLetterOrDigit))

object DotsSupplier :
    CharSupplier("Dots", intArrayOf(1092588, 1091464, *(716..853).toIntArray()).map(Int::toChar))