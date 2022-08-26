package xyz.xenondevs.vetric.supplier.impl

import xyz.xenondevs.vetric.supplier.CharSupplier

class BarcodeSupplier(min: Int, max: Int, countUp: Boolean = false) : CharSupplier("Barcode", min, max, listOf('I', 'l'), countUp) {
    constructor(defaultLength: Int = 20) : this(defaultLength, defaultLength)
}