package xyz.xenondevs.vetric.transformer

import xyz.xenondevs.vetric.config.type.TransformerConfig
import xyz.xenondevs.vetric.jvm.JavaArchive
import xyz.xenondevs.vetric.jvm.Resource
import xyz.xenondevs.vetric.transformer.TransformerPriority.NORMAL

abstract class ResourceTransformer(name: String, config: TransformerConfig, priority: TransformerPriority = NORMAL) : Transformer(name, config, priority) {
    
    override fun transformJar(jar: JavaArchive) {
        jar.resources.forEach(this::transform)
    }
    
    abstract fun transform(resource: Resource)
    
}