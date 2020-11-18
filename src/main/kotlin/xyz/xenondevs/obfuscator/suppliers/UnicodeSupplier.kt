package xyz.xenondevs.obfuscator.suppliers

import kotlin.random.Random

object UnicodeSupplier : StringSupplier("Unicode") {

    override fun randomString(length: Int): String {
        val builder = StringBuilder()
        while (builder.length < length) {
            val ch = Random.nextInt(Character.MAX_CODE_POINT).toChar()
            if (ch != '$' && ch.isDefined())
                builder.append(ch)
        }
        return builder.toString()
    }

}