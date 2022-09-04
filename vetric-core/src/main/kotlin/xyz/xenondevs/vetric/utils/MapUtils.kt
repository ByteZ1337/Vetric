package xyz.xenondevs.vetric.utils

fun <K, V> Map<K, V>.swapTo(
    mapSupplier: () -> MutableMap<V, MutableSet<K>>,
    setSupplier: () -> MutableSet<K>
): Map<V, Set<K>> {
    val map = mapSupplier()
    forEach { (key, value) ->
        map.getOrPut(value, setSupplier).add(key)
    }
    return map
}