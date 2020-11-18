package xyz.xenondevs.obfuscator.transformers

import xyz.xenondevs.obfuscator.transformers.misc.Cleaner
import xyz.xenondevs.obfuscator.transformers.renamer.Renamer
import xyz.xenondevs.obfuscator.transformers.renamer.ResourceUpdater
import xyz.xenondevs.obfuscator.transformers.string.StringEncrypter

object TransformerRegistry {
    
    val transformers = mutableListOf(
        Cleaner, Renamer, ResourceUpdater,
        StringEncrypter
    )
    
}