package xyz.xenondevs.vetric.transformer.impl.obfuscation.renamer

import xyz.xenondevs.bytebase.jvm.Resource
import xyz.xenondevs.vetric.transformer.ResourceTransformer
import xyz.xenondevs.vetric.transformer.TransformerPriority
import xyz.xenondevs.vetric.transformer.registry.get

class ResourceUpdater : ResourceTransformer("ResourceUpdater", TransformerPriority.LOWEST) {
    
    private var fileExtensions = hashSetOf("mf", "yml", "xml", "json")
    private val renamer by lazy {
        upperRegistry.get<Renamer>()
            ?: throw IllegalStateException("Renamer not found! (is it enabled?)")
    }
    
    override fun transform(resource: Resource) {
        if (resource.fileExtension.lowercase() in fileExtensions) {
            var content = resource.content.decodeToString()
            val sortedMappings = renamer.mappings.toSortedMap(compareByDescending { it.length })
            sortedMappings.forEach { (from, to) ->
                content = content.replace(from, to)
                content = content.replace(from.replace('/', '.'), to.replace('/', '.'))
            }
            resource.content = content.encodeToByteArray()
        }
    }
    
}