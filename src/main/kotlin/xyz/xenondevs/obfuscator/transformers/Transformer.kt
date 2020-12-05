package xyz.xenondevs.obfuscator.transformers

import xyz.xenondevs.obfuscator.config.type.TransformerType
import xyz.xenondevs.obfuscator.jvm.JavaArchive

abstract class Transformer(val name: String, val configType: TransformerType) {
    
    var enabled = false
    
    abstract fun transformJar(jar: JavaArchive)
    
    override fun hashCode() = name.hashCode()
    
    override fun equals(other: Any?) =
        this === other || (other is Transformer && other.name == this.name)
}