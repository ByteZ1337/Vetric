package xyz.xenondevs.vetric.transformer

import xyz.xenondevs.vetric.transformer.obfuscation.misc.CodeHider
import xyz.xenondevs.vetric.transformer.obfuscation.misc.EnumAccessHider
import xyz.xenondevs.vetric.transformer.obfuscation.number.NumberObfuscator
import xyz.xenondevs.vetric.transformer.obfuscation.renamer.Renamer
import xyz.xenondevs.vetric.transformer.obfuscation.renamer.ResourceUpdater
import xyz.xenondevs.vetric.transformer.obfuscation.renamer.Shuffler
import xyz.xenondevs.vetric.transformer.obfuscation.string.StringEncrypter
import xyz.xenondevs.vetric.transformer.shrinking.Cleaner

object TransformerRegistry {
    
    val transformers = listOf(
        Cleaner, Renamer, ResourceUpdater, StringEncrypter,
        Shuffler, EnumAccessHider, CodeHider, NumberObfuscator
    )
    
    fun getEnabled() = transformers.filter(Transformer::enabled)
    
}