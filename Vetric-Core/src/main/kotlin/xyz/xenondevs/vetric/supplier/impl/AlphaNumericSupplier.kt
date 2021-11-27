package xyz.xenondevs.vetric.supplier.impl

import xyz.xenondevs.vetric.supplier.CharSupplier

private val CHARS = ('a'..'z') + ('A'..'Z') + ('0'..'9')

class AlphaNumericSupplier(min: Int, max: Int) : CharSupplier("AlphaNumeric", min, max, CHARS) {
    constructor(defaultLength: Int = 20) : this(defaultLength, defaultLength)
}