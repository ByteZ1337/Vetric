package xyz.xenondevs.obfuscator

import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.tree.ClassNode
import java.io.File

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