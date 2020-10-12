package xyz.xenondevs.obfuscator.transformer

import xyz.xenondevs.obfuscator.jvm.JavaArchive

abstract class Transformer(val name: String) {

    abstract fun transformJar(jar: JavaArchive)

}