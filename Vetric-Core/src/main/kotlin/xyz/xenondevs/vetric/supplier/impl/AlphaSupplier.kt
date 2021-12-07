package xyz.xenondevs.vetric.supplier.impl

import xyz.xenondevs.vetric.supplier.CharSupplier

private val ALPHABET = ('a'..'z') + ('A'..'Z')

class AlphaSupplier(min: Int, max: Int, countUp: Boolean = false) : CharSupplier("Alpha", min, max, ALPHABET, countUp) {
    constructor(defaultLength: Int = 20) : this(defaultLength, defaultLength)
}