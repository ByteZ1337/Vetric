package xyz.xenondevs.vetric.supplier

abstract class StringSupplier(val name: String) {
    
    private val generated = HashSet<String>()
    
    abstract fun randomString(): String
    
    open fun randomStringUnique(exclude: HashSet<String> = generated): String {
        var random: String
        do random = randomString() while (exclude.contains(random))
        exclude += random
        return random
    }
    
}