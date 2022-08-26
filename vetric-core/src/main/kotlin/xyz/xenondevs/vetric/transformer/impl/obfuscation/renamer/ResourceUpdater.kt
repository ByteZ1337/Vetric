package xyz.xenondevs.vetric.transformer.impl.obfuscation.renamer

import xyz.xenondevs.bytebase.jvm.Resource
import xyz.xenondevs.vetric.transformer.ResourceTransformer
import xyz.xenondevs.vetric.transformer.TransformerPriority

object ResourceUpdater : ResourceTransformer("ResourceUpdater", TransformerPriority.LOWEST) {
    
    private var fileExtensions = hashSetOf("mf", "yml", "xml", "json")
    
    override fun transform(resource: Resource) {
        if (resource.fileExtension.lowercase() in fileExtensions) {
            var content = resource.content.decodeToString()
            val sortedMappings = Renamer.mappings.toSortedMap(compareByDescending { it.length })
            sortedMappings.forEach { (from, to) ->
                content = content.replace(from, to)
                content = content.replace(from.replace('/', '.'), to.replace('/', '.'))
            }
            resource.content = content.encodeToByteArray()
        }
    }
    
}