package xyz.xenondevs.vetric.supplier

import xyz.xenondevs.vetric.supplier.registry.SupplierRegistry
import xyz.xenondevs.vetric.supplier.registry.SupplierRegistry.SupplierInfo
import xyz.xenondevs.vetric.supplier.registry.SupplierType
import java.io.File

val DEFAULT_SUPPLIER = SupplierFactory(SupplierRegistry.ALPHA_SUPPLIER, SupplierType.CHAR.defaultConfig)

/**
 * Generates [StringSupplier].
 */
class SupplierFactory(private val supplierInfo: SupplierInfo, val config: SupplierConfig) {
    
    constructor(file: File, config: DictionarySupplierConfig) : this(SupplierInfo(file.nameWithoutExtension, SupplierType.DICTIONARY) { cfg, needed ->
        DictionarySupplier(file, (cfg as DictionarySupplierConfig).countUp, needed)
    }, config)
    
    fun create() = supplierInfo.constructor(config, 500)
    
    fun create(needed: Int) = supplierInfo.constructor(config, needed)
    
}