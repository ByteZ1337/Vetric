package xyz.xenondevs.vetric.transformer.impl.obfuscation.string

import xyz.xenondevs.vetric.transformer.SubTransformerRegistry
import xyz.xenondevs.vetric.transformer.TransformerPriority
import xyz.xenondevs.vetric.transformer.impl.obfuscation.string.pool.SingleStringPooler
import xyz.xenondevs.vetric.transformer.impl.obfuscation.string.pool.StringPooler

object StringObfuscator : SubTransformerRegistry<StringTransformer>("StringObfuscator", TransformerPriority.HIGHEST) {
    
    override val transformers = listOf(SingleStringPooler, StringPooler)
    
}