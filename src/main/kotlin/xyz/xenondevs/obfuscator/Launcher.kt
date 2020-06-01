package xyz.xenondevs.obfuscator

@ExperimentalStdlibApi
object Launcher {
    var OBFUSCATOR: Obfuscator? = null

    @JvmStatic
    fun main(args: Array<String>) {
        OBFUSCATOR = Obfuscator()
        OBFUSCATOR!!.run(args.joinToString(" "))
    }
}