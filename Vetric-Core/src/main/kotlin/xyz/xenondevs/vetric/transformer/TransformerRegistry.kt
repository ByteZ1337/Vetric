package xyz.xenondevs.vetric.transformer

import xyz.xenondevs.vetric.transformer.impl.obfuscation.misc.CodeHider
import xyz.xenondevs.vetric.transformer.impl.obfuscation.renamer.Renamer
import xyz.xenondevs.vetric.transformer.impl.obfuscation.renamer.ResourceUpdater
import xyz.xenondevs.vetric.transformer.impl.shrinking.LineNumberRemover

object TransformerRegistry : Iterable<Transformer> by sortedSetOf(
    LineNumberRemover, CodeHider, Renamer, ResourceUpdater
)