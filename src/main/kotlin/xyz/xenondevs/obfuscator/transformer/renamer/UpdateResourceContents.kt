package xyz.xenondevs.obfuscator.transformer.renamer

import xyz.xenondevs.obfuscator.Obfuscator
import xyz.xenondevs.obfuscator.asm.Resource
import xyz.xenondevs.obfuscator.transformer.ResourceTransformer

@ExperimentalStdlibApi
class UpdateResourceContents : ResourceTransformer("Resources Updater") {

    val fileNames = arrayOf("yml", "json", "txt", "xml", "mf")

    override fun transform(resource: Resource) {
        if (fileNames.any { resource.name.toLowerCase().endsWith(".$it") }) {
            var content = resource.content.decodeToString()
            val nameMap = Obfuscator.INSTANCE.transformerRegistry.getTransformer<ClassRenamer>().nameMap
            nameMap.toSortedMap(compareByDescending(String::length)).forEach { (old, new) ->
                run {
                    content = content.replace(old, new)
                    content = content.replace(old.replace("/", "."), new.replace("/", "."))
                }
            }
            resource.content = content.encodeToByteArray()
            println("Processed ${resource.name}")
        }
    }
}