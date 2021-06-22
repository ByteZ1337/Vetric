package xyz.xenondevs.vetric.supplier

import kotlin.random.Random

// TODO increasing
open class CharSupplier(name: String, private val min: Int, private val max: Int, private val chars: List<Char>) : StringSupplier(name) {
    
    constructor(name: String, defaultLength: Int = 20, chars: List<Char>) : this(name, defaultLength, defaultLength, chars)
    
    constructor(name: String, min: Int, max: Int, codes: Iterable<Int>) : this(name, min, max, codes.map(Int::toChar))
    
    constructor(name: String, defaultLength: Int = 20, codes: Iterable<Int>) : this(name, defaultLength, defaultLength, codes.map(Int::toChar))
    
    override fun randomString(length: Int) = (1..length).joinToString("") { chars.random().toString() }
    
    override fun randomString() = randomString(Random.nextInt(min, max + 1))
}

class AlphaSupplier(min: Int, max: Int) : CharSupplier("Alpha", min, max, ('a'..'z') + ('A'..'Z')) {
    constructor(defaultLength: Int = 20) : this(defaultLength, defaultLength)
}

class AlphaNumericSupplier(min: Int, max: Int) : CharSupplier("AlphaNumeric", min, max, ('a'..'z') + ('A'..'Z') + ('0'..'9')) {
    constructor(defaultLength: Int = 20) : this(defaultLength, defaultLength)
}

class BarcodeSupplier(min: Int, max: Int) : CharSupplier("Barcode", min, max, listOf('I', 'l')) {
    constructor(defaultLength: Int = 20) : this(defaultLength, defaultLength)
}

class DotsSupplier(min: Int, max: Int) : CharSupplier("Dots", min, max, (0x2cc..0x355) + 0x10a788 + 0x10abec) {
    constructor(defaultLength: Int = 20) : this(defaultLength, defaultLength)
}

class InvisibleSupplier(min: Int, max: Int) : CharSupplier("Invisible", min, max, 0x2000..0x200F) {
    constructor(defaultLength: Int = 20) : this(defaultLength, defaultLength)
}

enum class Supplier(private val constructor: (Int, Int) -> StringSupplier) {
    ALPHA(::AlphaSupplier),
    ALPHANUMERIC(::AlphaNumericSupplier),
    BARCODE(::BarcodeSupplier),
    COMBINING(::CombiningSupplier),
    DOTS(::DotsSupplier),
    INVISIBLE(::InvisibleSupplier),
    UNICODE(::UnicodeSupplier);
    
    companion object {
        private val VALUES = values()
        
        operator fun get(name: String) = VALUES.firstOrNull { it.toString().equals(name, true) }
    }
    
    fun newInstance(min: Int, max: Int): StringSupplier {
        return constructor(min, max)
    }
    
}