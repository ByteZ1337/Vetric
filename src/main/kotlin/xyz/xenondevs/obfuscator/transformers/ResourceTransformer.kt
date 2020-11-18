package xyz.xenondevs.obfuscator.transformers

import xyz.xenondevs.obfuscator.jvm.JavaArchive
import xyz.xenondevs.obfuscator.jvm.Resource

abstract class ResourceTransformer(name: String) : Transformer(name) {

    override fun transformJar(jar: JavaArchive) {
        jar.resources.forEach(this::transform)
    }

    abstract fun transform(resource: Resource)

}