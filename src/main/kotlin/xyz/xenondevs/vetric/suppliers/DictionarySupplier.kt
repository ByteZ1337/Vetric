package xyz.xenondevs.vetric.suppliers

// TODO add option for randomizing
open class DictionarySupplier(name: String, private val dictionary: List<String>) : StringSupplier(name) {
    
    companion object {
        val DEFAULT = listOf<DictionarySupplier>()
    }
    
    private var index = 0
    
    override fun randomString(): String {
        if (index >= dictionary.size) index = 0
        return dictionary[index++]
    }
    
    override fun randomStringUnique(exclude: HashSet<String>): String {
        if (exclude.containsAll(dictionary))
            error("Not enough entries in dictionary $name!")
        return dictionary.first { !exclude.contains(it) }.apply(exclude::add)
    }
    
    @Deprecated("The length parameter is unused.", ReplaceWith("randomString()"))
    override fun randomString(length: Int) = randomString()
    
    @Deprecated("The range parameter is unused.", ReplaceWith("randomString()"))
    override fun randomString(range: IntRange) = randomString()
    
    @Deprecated("The length parameter is unused.", ReplaceWith("randomStringUnique(exclude)"))
    override fun randomStringUnique(length: Int, exclude: HashSet<String>) = randomStringUnique(exclude)
    
    @Deprecated("The range parameter is unused.", ReplaceWith("randomStringUnique(exclude)"))
    override fun randomStringUnique(range: IntRange, exclude: HashSet<String>) = randomStringUnique(exclude)
    
}