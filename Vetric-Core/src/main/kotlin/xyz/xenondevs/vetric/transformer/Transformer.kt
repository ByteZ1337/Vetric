package xyz.xenondevs.vetric.transformer

import xyz.xenondevs.bytebase.jvm.JavaArchive
import xyz.xenondevs.vetric.config.JsonConfig

abstract class Transformer(
    val name: String,
    val priority: TransformerPriority
) : Comparable<Transformer> {
    
    open fun loadConfig(config: JsonConfig) {}
    
    open fun prepare(archive: JavaArchive) {}
    
    abstract fun transform(archive: JavaArchive)
    
    override fun hashCode() = name.hashCode()
    
    override fun equals(other: Any?) =
        this === other || (other is Transformer && other.name == name)
    
    override fun compareTo(other: Transformer): Int {
        if (equals(other)) return 0
        
        val result = priority.compareTo(other.priority)
        return if (result == 0) 1 else result
    }
    
}