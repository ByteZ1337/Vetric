package xyz.xenondevs.vetric.supplier.impl

import xyz.xenondevs.vetric.supplier.CharSupplier

private val NUMERIC = ('0'..'9').toList()

class NumericSupplier(min: Int, max: Int, countUp: Boolean = false) : CharSupplier("Numeric", min, max, NUMERIC, countUp)