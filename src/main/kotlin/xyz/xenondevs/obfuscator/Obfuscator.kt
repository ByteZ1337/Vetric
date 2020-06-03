package xyz.xenondevs.obfuscator

import xyz.xenondevs.obfuscator.asm.SmartJar
import xyz.xenondevs.obfuscator.transformer.TransformerRegistry
import java.io.File
import java.io.IOException

@ExperimentalStdlibApi
class Obfuscator {

    init {
        INSTANCE = this
    }

    val transformerRegistry = TransformerRegistry()
    private var currentJar: SmartJar = SmartJar()

    fun run(path: String) {
        try {
            currentJar.readFile(File(path))
        } catch (ex: IOException) {
            ex.printStackTrace()
        }
        transformerRegistry.transformers.forEach(currentJar::apply)
        currentJar.writeFile(File("out.jar"))
    }

    companion object {
        lateinit var INSTANCE: Obfuscator
    }

}