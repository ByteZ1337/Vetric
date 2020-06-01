package xyz.xenondevs.obfuscator.tansformer

import xyz.xenondevs.obfuscator.asm.SmartJar

@ExperimentalStdlibApi
abstract class Transformer(val name: String) {

    abstract fun transform(jar: SmartJar)
}