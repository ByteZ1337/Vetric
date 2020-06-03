package xyz.xenondevs.obfuscator.transformer.renamer

import xyz.xenondevs.obfuscator.Obfuscator
import xyz.xenondevs.obfuscator.asm.Resource
import xyz.xenondevs.obfuscator.transformer.ResourceTransformer

@ExperimentalStdlibApi
class UpdateResourceContents : ResourceTransformer("Plugin Yaml Changer") {

    override fun transform(resource: Resource) {
        if (resource.name.endsWith(".yml")) {
            var content = resource.content.decodeToString()
            val nameMap = (Obfuscator.INSTANCE.transformerRegistry.getTransformer(ClassRenamer::class.java) as ClassRenamer).nameMap
            nameMap.forEach { (old, new) ->
                run {
                    content = content.replace(old, new)
                    content = content.replace(old.replace("/", "."), new)
                }
            }
            resource.content = content.encodeToByteArray()
        }
    }
}