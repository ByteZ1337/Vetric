package xyz.xenondevs.vetric.supplier.impl

import xyz.xenondevs.vetric.supplier.CharSupplier

class BarcodeSupplier(min: Int, max: Int) : CharSupplier("Barcode", min, max, listOf('I', 'l')) {
    constructor(defaultLength: Int = 20) : this(defaultLength, defaultLength)
}