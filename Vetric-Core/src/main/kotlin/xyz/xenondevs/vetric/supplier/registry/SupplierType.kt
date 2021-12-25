package xyz.xenondevs.vetric.supplier.registry

import xyz.xenondevs.vetric.supplier.CharSupplierConfig
import xyz.xenondevs.vetric.supplier.DictionarySupplierConfig
import xyz.xenondevs.vetric.supplier.NormalSupplierConfig
import xyz.xenondevs.vetric.supplier.SupplierConfig

enum class SupplierType(val defaultConfig: SupplierConfig) {
    /**
     * A normal supplier implementation that supports a min and max length
     */
    NORMAL(NormalSupplierConfig(10, 20)),
    
    /**
     * A supplier implementation that supports a min and max length, a character set and the option
     * to "count up". Example: ``a, b, ..., aa, ab, ...``
     */
    CHAR(CharSupplierConfig(1, 1, true)),
    
    /**
     * A supplier that has a ``List<String>`` of values.
     */
    DICTIONARY(DictionarySupplierConfig(true));
    
}