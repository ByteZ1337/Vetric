package xyz.xenondevs.vetric.transformer

import xyz.xenondevs.bytebase.jvm.JavaArchive
import xyz.xenondevs.bytebase.jvm.Resource

abstract class ResourceTransformer(name: String, priority: TransformerPriority) : Transformer(name, priority) {
    
    override fun transform(jar: JavaArchive) {
        jar.resources.forEach(::transform)
    }
    
    abstract fun transform(resource: Resource)
    
}