package xyz.xenondevs.vetric.supplier

import xyz.xenondevs.vetric.supplier.registry.SupplierRegistry
import xyz.xenondevs.vetric.supplier.registry.SupplierRegistry.SupplierInfo

val DEFAULT_SUPPLIER = SupplierFactory(SupplierRegistry.ALPHA_SUPPLIER, SupplierConfig(20, 20, true))

/**
 * Generates [StringSupplier].
 */
class SupplierFactory(val supplierInfo: SupplierInfo, val config: SupplierConfig) {
    
    fun create() = supplierInfo.constructor(config)
    
}