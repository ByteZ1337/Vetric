package xyz.xenondevs.obfuscator

import xyz.xenondevs.obfuscator.jvm.ClassPath
import xyz.xenondevs.obfuscator.jvm.JavaArchive
import xyz.xenondevs.obfuscator.transformer.TransformerRegistry
import java.io.File

object Obfuscator {

    lateinit var jar: JavaArchive

    fun run() {
        ClassPath.reset()
        jar = JavaArchive(File("in.jar"), true)
        TransformerRegistry.transformers.forEach { it.transformJar(jar) }
        println("Writing file...")
        jar.writeFile(File("out.jar"))
    }
}