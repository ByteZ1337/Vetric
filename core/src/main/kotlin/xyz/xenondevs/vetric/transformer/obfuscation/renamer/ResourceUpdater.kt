package xyz.xenondevs.vetric.transformer.obfuscation.renamer

import com.google.gson.JsonObject
import xyz.xenondevs.vetric.config.type.TransformerConfig
import xyz.xenondevs.vetric.jvm.Resource
import xyz.xenondevs.vetric.transformer.ResourceTransformer
import xyz.xenondevs.vetric.util.json.hasArray
import xyz.xenondevs.vetric.util.json.toStringList

object ResourceUpdater : ResourceTransformer("ResourceUpdater", ResourceUpdaterConfig) {
    
    private var fileExtensions = hashSetOf("mf", "yml", "xml", "json")
    
    override fun transform(resource: Resource) {
        if (fileExtensions.contains(resource.getExtension().lowercase())) {
            var content = resource.content.decodeToString()
            val nameMap = Renamer.mappings.toList().sortedByDescending { it.first.length }.toMap()
            nameMap.forEach { (oldName, newName) ->
                content = content.replace(oldName, newName)
                content = content.replace(oldName.replace('/', '.'), newName.replace('/', '.'))
            }
            resource.content = content.encodeToByteArray()
            println("Processed ${resource.fileName}")
        }
    }
    
    private object ResourceUpdaterConfig : TransformerConfig(ResourceUpdater::class) {
        
        override fun parse(obj: JsonObject) {
            super.parse(obj)
            if (enabled && obj.hasArray("filetypes"))
                fileExtensions = obj.getAsJsonArray("filetypes").toStringList(::HashSet)
        }
    }
}