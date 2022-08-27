package xyz.xenondevs.vetric.transformer.impl.obfuscation.string

import xyz.xenondevs.vetric.transformer.SubTransformerRegistry
import xyz.xenondevs.vetric.transformer.TransformerPriority
import xyz.xenondevs.vetric.transformer.impl.obfuscation.string.pool.SingleStringPooler
import xyz.xenondevs.vetric.transformer.impl.obfuscation.string.pool.StringPooler

class StringObfuscator : SubTransformerRegistry<StringTransformer>("StringObfuscator", TransformerPriority.HIGHEST) {
    
    init {
        register("StringPooler", ::StringPooler)
        register("SingleStringPooler", ::SingleStringPooler)
    }
    
}