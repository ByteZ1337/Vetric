package xyz.xenondevs.vetric.supplier

open class SupplierConfig

class NormalSupplierConfig(val min: Int, val max: Int): SupplierConfig()

class CharSupplierConfig(val min: Int, val max: Int, val countUp: Boolean): SupplierConfig()

class DictionarySupplierConfig(val countUp: Boolean): SupplierConfig()