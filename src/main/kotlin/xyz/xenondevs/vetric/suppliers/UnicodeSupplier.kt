package xyz.xenondevs.vetric.suppliers

import kotlin.random.Random

class UnicodeSupplier(private val min: Int, private val max: Int) : StringSupplier("Unicode") {
    
    constructor(defaultLength: Int = 20) : this(defaultLength, defaultLength)
    
    override fun randomString(length: Int): String {
        val builder = StringBuilder()
        while (builder.length < length) {
            val ch = Random.nextInt(Character.MAX_CODE_POINT).toChar()
            if (ch != '$' && ch.isDefined())
                builder.append(ch)
        }
        return builder.toString()
    }
    
    override fun randomString() = randomString(Random.nextInt(min, max + 1))
    
}