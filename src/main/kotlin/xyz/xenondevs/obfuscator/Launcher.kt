package xyz.xenondevs.obfuscator

import org.objectweb.asm.tree.ClassNode

@ExperimentalStdlibApi
object Launcher {
    lateinit var OBFUSCATOR: Obfuscator

    @JvmStatic
    fun main(args: Array<String>) {
        OBFUSCATOR = Obfuscator()
        OBFUSCATOR.run(args.joinToString(" "))
    }
}