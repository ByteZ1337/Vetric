package xyz.xenondevs.obfuscator

import org.objectweb.asm.tree.ClassNode

@ExperimentalStdlibApi
object Launcher {
    lateinit var OBFUSCATOR: Obfuscator

    @JvmStatic
    fun main(args: Array<String>) {
        val node = ClassNode()
        OBFUSCATOR = Obfuscator()
        OBFUSCATOR.run(args.joinToString(" "))
    }
}