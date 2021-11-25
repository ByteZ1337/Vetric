package xyz.xenondevs.vetric.transformer

import xyz.xenondevs.vetric.transformer.impl.shrinking.LineNumberRemover

object TransformerRegistry : Iterable<Transformer> by sortedSetOf(
    LineNumberRemover
)