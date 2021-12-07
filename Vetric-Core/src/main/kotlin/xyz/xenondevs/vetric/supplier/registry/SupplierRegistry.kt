package xyz.xenondevs.vetric.supplier.registry

import xyz.xenondevs.vetric.supplier.CharSupplier
import xyz.xenondevs.vetric.supplier.StringSupplier
import xyz.xenondevs.vetric.supplier.SupplierConfig
import xyz.xenondevs.vetric.supplier.impl.*

private typealias SupplierConstructor = (SupplierConfig) -> StringSupplier
private typealias CharSupplierConstructor = (min: Int, max: Int, countUp: Boolean) -> CharSupplier

/**
 * Registry for default supplier implementations
 */
object SupplierRegistry {
    
    private val suppliers = ArrayList<SupplierInfo>()
    
    val ALPHA_SUPPLIER = registerCharSupplier("Alpha", ::AlphaSupplier)
    val ALPHA_NUMERIC_SUPPLIER = registerCharSupplier("AlphaNumeric", ::AlphaNumericSupplier)
    val NUMERIC_SUPPLIER = registerCharSupplier("Numeric", ::NumericSupplier)
    val BARCODE_SUPPLIER = registerCharSupplier("Barcode", ::BarcodeSupplier)
    val DOTS_SUPPLIER = registerCharSupplier("Dots", ::DotsSupplier)
    val INVISIBLE_SUPPLIER = registerCharSupplier("Invisible", ::InvisibleSupplier)
    val COMBINING_SUPPLIER = register("Combining", SupplierType.NORMAL) { CombiningSupplier(it.min, it.max) }
    val UNICODE_SUPPLIER = register("Unicode", SupplierType.NORMAL) { UnicodeSupplier(it.min, it.max) }
    
    fun register(name: String, type: SupplierType, constructor: SupplierConstructor): SupplierInfo {
        val info = SupplierInfo(name, type, constructor)
        suppliers += info
        return info
    }
    
    fun registerCharSupplier(name: String, constructor: CharSupplierConstructor): SupplierInfo {
        return register(name, SupplierType.CHAR) { config -> constructor(config.min, config.max, config.countUp!!) }
    }
    
    fun getSupplier(name: String): SupplierInfo? {
        return suppliers.find { name.equals(it.name, ignoreCase = true) }
    }
    
    class SupplierInfo(val name: String, val type: SupplierType, val constructor: (SupplierConfig) -> StringSupplier)
    
}