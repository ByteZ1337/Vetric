package xyz.xenondevs.vetric.supplier

import xyz.xenondevs.vetric.supplier.registry.SupplierRegistry.SupplierInfo

/**
 * Generates [StringSupplier].
 */
class SupplierFactory(val supplierInfo: SupplierInfo, val config: SupplierConfig) {
    
    fun create() = supplierInfo.constructor(config)
    
}