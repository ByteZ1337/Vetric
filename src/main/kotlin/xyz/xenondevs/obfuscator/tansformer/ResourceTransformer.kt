package xyz.xenondevs.obfuscator.tansformer

import xyz.xenondevs.obfuscator.asm.Resource
import xyz.xenondevs.obfuscator.asm.SmartJar

@ExperimentalStdlibApi
abstract class ResourceTransformer(name: String) : Transformer(name) {

    override fun transform(jar: SmartJar) {
        jar.resources.forEach { this.transform(it); jar.files.remove(it.originalName); jar.files.remove(it.name); jar.files[it.name] = it.content }
    }

    abstract fun transform(resource: Resource)

}