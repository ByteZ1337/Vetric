package xyz.xenondevs.vetric.transformer

import xyz.xenondevs.bytebase.jvm.JavaArchive
import xyz.xenondevs.vetric.Vetric
import xyz.xenondevs.vetric.config.JsonConfig
import xyz.xenondevs.vetric.config.VetricConfig
import xyz.xenondevs.vetric.transformer.registry.TransformerRegistry

abstract class Transformer(
    val name: String,
    internal var priority: TransformerPriority
) : Comparable<Transformer> {
    
    lateinit var vetric: Vetric
    
    lateinit var upperRegistry: TransformerRegistry<out Transformer>
    
    val logger by lazy { vetric.logger }
    
    open fun loadConfig(config: JsonConfig, vetricConfig: VetricConfig) {}
    
    open fun prepare(jar: JavaArchive) {}
    
    abstract fun transform(jar: JavaArchive)
    
    override fun hashCode() = name.hashCode()
    
    override fun equals(other: Any?) =
        this === other || (other is Transformer && other.name == name)
    
    override fun compareTo(other: Transformer): Int {
        if (equals(other)) return 0
        
        val result = priority.compareTo(other.priority)
        return if (result == 0) 1 else result
    }
    
    override fun toString() = "Transformer(name='$name', priority=$priority)"
    
}