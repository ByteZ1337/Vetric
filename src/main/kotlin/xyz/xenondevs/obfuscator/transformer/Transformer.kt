package xyz.xenondevs.obfuscator.transformer

import xyz.xenondevs.obfuscator.asm.SmartJar

@ExperimentalStdlibApi
abstract class Transformer(val name: String) {

    abstract fun transform(jar: SmartJar)
}