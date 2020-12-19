package xyz.xenondevs.vetric.transformers.renamer

import com.google.gson.JsonObject
import xyz.xenondevs.vetric.config.type.TransformerConfig
import xyz.xenondevs.vetric.jvm.Resource
import xyz.xenondevs.vetric.transformers.ResourceTransformer
import xyz.xenondevs.vetric.utils.json.hasArray
import xyz.xenondevs.vetric.utils.json.toStringList

object ResourceUpdater : ResourceTransformer("ResourceUpdater", ResourceUpdaterConfig) {
    
    private var fileExtensions = hashSetOf("mf", "yml", "xml", "cfg", "json", "txt")
    
    override fun transform(resource: Resource) {
        if (fileExtensions.contains(resource.getExtension().toLowerCase())) {
            var content = resource.content.decodeToString()
            val nameMap = Renamer.mappings.toList().sortedByDescending { it.first.length }.toMap()
            nameMap.forEach { (oldName, newName) ->
                content = content.replace(oldName, newName)
                content = content.replace(oldName.replace('/', '.'), newName.replace('/', '.'))
            }
            resource.content = content.encodeToByteArray()
            println("Processed " + resource.fileName)
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