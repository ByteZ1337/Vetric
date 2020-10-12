package xyz.xenondevs.obfuscator.supplier

object CombiningSupplier : StringSupplier("Combining") {

    private val combining by lazy {
        intArrayOf(
            0x300, 0x301, 0x302, 0x303, 0x304,
            0x305, 0x306, 0x307, 0x308, 0x30A,
            0x30B, 0x30C, 0x30F, 0x313, 0x314,
            0x326, 0x327, 0x335, 0x336, 0x342,
            0x345
        ).map(Int::toChar)
    }

    override fun randomString(length: Int): String {
        // TODO
        return ""
    }

}