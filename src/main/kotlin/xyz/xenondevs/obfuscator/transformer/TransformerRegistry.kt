package xyz.xenondevs.obfuscator.transformer

import xyz.xenondevs.obfuscator.transformer.misc.Cleaner
import xyz.xenondevs.obfuscator.transformer.renamer.Renamer
import xyz.xenondevs.obfuscator.transformer.renamer.ResourceUpdater
import xyz.xenondevs.obfuscator.transformer.string.StringEncrypter

object TransformerRegistry {

    val transformers = listOf(
        Cleaner, Renamer, ResourceUpdater,
        StringEncrypter
    )

}