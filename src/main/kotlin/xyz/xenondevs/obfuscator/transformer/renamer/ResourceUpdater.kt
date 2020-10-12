package xyz.xenondevs.obfuscator.transformer.renamer

import xyz.xenondevs.obfuscator.jvm.Resource
import xyz.xenondevs.obfuscator.transformer.ResourceTransformer

object ResourceUpdater : ResourceTransformer("ResourceUpdater") {

    private val FILE_EXTENSION = hashSetOf("mf", "yml", "xml", "cfg", "json", "txt")

    override fun transform(resource: Resource) {
        if (FILE_EXTENSION.contains(resource.getExtension().toLowerCase())) {
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

}