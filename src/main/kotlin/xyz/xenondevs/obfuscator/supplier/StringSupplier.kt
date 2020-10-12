package xyz.xenondevs.obfuscator.supplier

abstract class StringSupplier(val name: String) {

    internal val generated = HashSet<String>()

    abstract fun randomString(length: Int): String

    fun randomString(range: IntRange) = randomString(range.random())

    fun randomString() = randomString(10..20)

    fun randomStringUnique(length: Int, exclude: HashSet<String> = generated): String {
        var random: String
        do random = randomString(length) while (exclude.contains(random))
        exclude += random
        return random
    }

    fun randomStringUnique(range: IntRange, exclude: HashSet<String> = generated) =
        randomStringUnique(range.random(), exclude)

    fun randomStringUnique(exclude: HashSet<String> = generated) =
        randomStringUnique(10..20, exclude)

}
