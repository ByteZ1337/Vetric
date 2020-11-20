package xyz.xenondevs.obfuscator.suppliers

open class DictionarySupplier(name: String, val dictionary: List<String>) : StringSupplier(name) {

    companion object {
        val DEFAULT = listOf<DictionarySupplier>()
    }
    
    private var index = 0

    override fun randomString(): String {
        if (index >= dictionary.size) index = 0
        return dictionary[index++]
    }

    fun randomStringUnique(exclude: HashSet<String> = generated): String {
        if (exclude.containsAll(dictionary))
            error("Not enough entries in dictionary $name!")
        return dictionary.filter { !exclude.contains(it) }.random().also { exclude.add(it) }
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