package xyz.xenondevs.vetric.supplier

abstract class StringSupplier(val name: String) {
    
    private val generated = HashSet<String>()
    
    abstract fun randomString(length: Int): String
    
    abstract fun randomString(): String
    
    open fun randomString(range: IntRange = 10..20) = randomString(range.random())
    
    open fun randomStringUnique(length: Int, exclude: HashSet<String> = generated): String {
        var random: String
        do random = randomString(length) while (exclude.contains(random))
        exclude += random
        return random
    }
    
    open fun randomStringUnique(exclude: HashSet<String> = generated): String {
        var random: String
        do random = randomString() while (exclude.contains(random))
        exclude += random
        return random
    }
    
    open fun randomStringUnique(range: IntRange, exclude: HashSet<String> = generated) =
        randomStringUnique(range.random(), exclude)
    
}