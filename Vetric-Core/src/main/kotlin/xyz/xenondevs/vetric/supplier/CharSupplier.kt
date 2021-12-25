package xyz.xenondevs.vetric.supplier

import kotlin.math.ceil
import kotlin.math.log
import kotlin.random.Random

private val UINT_MAX = UInt.MAX_VALUE.toDouble()

/**
 * StringSupplier implementation that uses a list of characters to generate a random string. This implementation also
 * supports "counting up". For example: a, b, c, ..., z, aa, ab, ac, ..., zz, aaa, aab, aac, ..., zzz, etc.
 */
open class CharSupplier(name: String,
                        private val min: Int,
                        private val max: Int,
                        private val chars: List<Char>,
                        private val countUp: Boolean = false
) : StringSupplier(name) {
    
    private val maxLength = ceil(log(UINT_MAX, chars.size.toDouble())).toInt()
    
    private var index = -1
    
    constructor(name: String, min: Int, max: Int, codes: Iterable<Int>) : this(name, min, max, codes.map(Int::toChar))
    
    constructor(name: String, min: Int, max: Int, codes: CharRange) : this(name, min, max, codes.toList())
    
    override fun randomString(length: Int) = buildString { repeat(length) { append(chars.random()) } }
    
    override fun randomString() = randomString(Random.nextInt(min, max + 1))
    
    override fun randomStringUnique(exclude: HashSet<String>): String {
        return if (countUp) stringAt(++index) else super.randomStringUnique(exclude)
    }
    
    override fun randomStringUnique(length: Int, exclude: HashSet<String>): String {
        return if (countUp) stringAt(++index) else super.randomStringUnique(exclude)
    }
    
    open fun stringAt(index: Int): String {
        val charsLength = chars.size
        var i = index
        val buf = CharArray(maxLength + 1)
        var charPos = maxLength
        var t = false
        
        if (i > 0) i = -i
        
        while (i <= -charsLength) {
            t = true
            buf[charPos--] = chars[-(i % charsLength)]
            i /= charsLength
        }
        i = if (t) -(i + 1) else -i
        buf[charPos] = chars[i]
        
        return String(buf, charPos, maxLength + 1 - charPos)
    }
    
    open fun nextString() = stringAt(++index)
}