package xyz.xenondevs.vetric.transformers

import xyz.xenondevs.vetric.config.type.TransformerConfig
import xyz.xenondevs.vetric.jvm.JavaArchive
import xyz.xenondevs.vetric.transformers.TransformerPriority.NORMAL

abstract class Transformer(val name: String, val config: TransformerConfig, val priority: TransformerPriority = NORMAL) {
    
    var enabled = false
    
    open fun runPreparations(jar: JavaArchive) {}
    
    abstract fun transformJar(jar: JavaArchive)
    
    override fun hashCode() = name.hashCode()
    
    override fun equals(other: Any?) =
        this === other || (other is Transformer && other.name == this.name)
}