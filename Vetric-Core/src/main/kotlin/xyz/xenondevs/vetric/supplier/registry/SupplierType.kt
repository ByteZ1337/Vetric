package xyz.xenondevs.vetric.supplier.registry

enum class SupplierType {
    /**
     * A normal supplier implementation that supports a min and max length
     */
    NORMAL,
    
    /**
     * A supplier implementation that supports a min and max length, a character set and the option
     * to "count up". Example: ``a, b, ..., aa, ab, ...``
     */
    CHAR,
    
    /**
     * A supplier that has a ``List<String>`` of values.
     */
    DICTIONARY,
}