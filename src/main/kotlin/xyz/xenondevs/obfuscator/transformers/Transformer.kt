package xyz.xenondevs.obfuscator.transformers

import xyz.xenondevs.obfuscator.jvm.JavaArchive

abstract class Transformer(val name: String) {

    abstract fun transformJar(jar: JavaArchive)

}