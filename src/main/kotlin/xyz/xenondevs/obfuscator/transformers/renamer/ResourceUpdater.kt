package xyz.xenondevs.obfuscator.transformers.renamer

import com.google.gson.JsonObject
import xyz.xenondevs.obfuscator.config.type.TransformerType
import xyz.xenondevs.obfuscator.jvm.Resource
import xyz.xenondevs.obfuscator.transformers.ResourceTransformer
import xyz.xenondevs.obfuscator.utils.json.hasArray
import xyz.xenondevs.obfuscator.utils.json.toStringList

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
    
    private object ResourceUpdaterConfig : TransformerType(ResourceUpdater::class) {
        override fun parse(obj: JsonObject) {
            super.parse(obj)
            if (enabled && obj.hasArray("filetypes"))
                fileExtensions = obj.getAsJsonArray("filetypes").toStringList(::HashSet)
        }
    }
}