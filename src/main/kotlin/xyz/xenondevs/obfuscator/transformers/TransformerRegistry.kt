package xyz.xenondevs.obfuscator.transformers

import xyz.xenondevs.obfuscator.transformers.misc.Cleaner
import xyz.xenondevs.obfuscator.transformers.renamer.Renamer
import xyz.xenondevs.obfuscator.transformers.renamer.ResourceUpdater
import xyz.xenondevs.obfuscator.transformers.string.StringEncrypter

object TransformerRegistry {
    
    val transformers = listOf(
        Cleaner, Renamer, ResourceUpdater,
        StringEncrypter
    )
    
    fun getEnabled() = transformers.filter(Transformer::enabled)
    
}