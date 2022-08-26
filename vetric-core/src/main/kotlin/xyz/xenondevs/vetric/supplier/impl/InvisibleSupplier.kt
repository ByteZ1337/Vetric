package xyz.xenondevs.vetric.supplier.impl

import xyz.xenondevs.vetric.supplier.CharSupplier

private val INVISIBLE = (0x2000..0x200F).map(Int::toChar)

class InvisibleSupplier(min: Int, max: Int, countUp: Boolean = false) : CharSupplier("Invisible", min, max, INVISIBLE, countUp) {
    constructor(defaultLength: Int = 20) : this(defaultLength, defaultLength)
}