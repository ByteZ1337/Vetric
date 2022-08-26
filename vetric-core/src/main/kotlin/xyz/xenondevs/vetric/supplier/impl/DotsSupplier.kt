package xyz.xenondevs.vetric.supplier.impl

import xyz.xenondevs.vetric.supplier.CharSupplier

private val DOTS = ((0x2cc..0x355) + 0x10a788 + 0x10abec).map(Int::toChar)

class DotsSupplier(min: Int, max: Int, countUp: Boolean = false) : CharSupplier("Dots", min, max, DOTS, countUp) {
    constructor(defaultLength: Int = 20) : this(defaultLength, defaultLength)
}