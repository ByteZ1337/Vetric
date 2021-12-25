package xyz.xenondevs.vetric.supplier.registry

import xyz.xenondevs.vetric.supplier.*
import xyz.xenondevs.vetric.supplier.impl.*

private typealias SupplierConstructor = (config: SupplierConfig, needed: Int) -> StringSupplier
private typealias NormalConstructor = (min: Int, max: Int) -> StringSupplier
private typealias CharSupplierConstructor = (min: Int, max: Int, countUp: Boolean) -> CharSupplier
private typealias DictionarySupplierConstructor = (countUp: Boolean, needed: Int) -> CharSupplier

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
    val COMBINING_SUPPLIER = registerNormalSupplier("Combining", ::CombiningSupplier)
    val UNICODE_SUPPLIER = registerNormalSupplier("Unicode", ::UnicodeSupplier)
    
    private fun register(name: String, type: SupplierType, constructor: SupplierConstructor): SupplierInfo {
        val info = SupplierInfo(name, type, constructor)
        suppliers += info
        return info
    }
    
    private fun registerNormalSupplier(name: String, constructor: NormalConstructor): SupplierInfo {
        return register(name, SupplierType.NORMAL) { config, _ ->
            if (config !is NormalSupplierConfig)
                throw IllegalArgumentException("Supplier config is not a NormalSupplierConfig")
            
            return@register constructor(config.min, config.max)
        }
    }
    
    private fun registerCharSupplier(name: String, constructor: CharSupplierConstructor): SupplierInfo {
        return register(name, SupplierType.CHAR) { config, _ ->
            if (config !is CharSupplierConfig)
                throw IllegalStateException("Supplier config is not a CharSupplierConfig")
            
            return@register constructor(config.min, config.max, config.countUp)
        }
    }
    
    private fun registerDictionarySupplier(name: String, constructor: DictionarySupplierConstructor): SupplierInfo {
        return register(name, SupplierType.DICTIONARY) { config, needed ->
            if (config !is DictionarySupplierConfig)
                throw IllegalStateException("Supplier config is not a DictionarySupplierConfig")
            
            return@register constructor(config.countUp, needed)
        }
    }
    
    fun getSupplier(name: String): SupplierInfo? =
        suppliers.find { name.equals(it.name, ignoreCase = true) }
    
    class SupplierInfo(val name: String, val type: SupplierType, val constructor: SupplierConstructor)
    
}