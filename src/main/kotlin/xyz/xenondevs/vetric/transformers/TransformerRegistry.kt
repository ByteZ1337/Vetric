package xyz.xenondevs.vetric.transformers

import xyz.xenondevs.vetric.transformers.misc.Cleaner
import xyz.xenondevs.vetric.transformers.misc.CodeHider
import xyz.xenondevs.vetric.transformers.misc.EnumAccessHider
import xyz.xenondevs.vetric.transformers.renamer.Renamer
import xyz.xenondevs.vetric.transformers.renamer.ResourceUpdater
import xyz.xenondevs.vetric.transformers.renamer.Shuffler
import xyz.xenondevs.vetric.transformers.string.StringEncrypter

object TransformerRegistry {
    
    val transformers = listOf(
        Cleaner, Renamer, ResourceUpdater,
        StringEncrypter, Shuffler, EnumAccessHider,
        CodeHider
    )
    
    fun getEnabled() = transformers.filter(Transformer::enabled)
    
}