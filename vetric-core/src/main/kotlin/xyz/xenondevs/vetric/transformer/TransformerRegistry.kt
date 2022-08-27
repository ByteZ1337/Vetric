package xyz.xenondevs.vetric.transformer

import kotlin.reflect.KClass

interface TransformerRegistry<T : Transformer> {
    
    operator fun get(name: String): T?
    
    fun getInfo(name: String): TransformerInfo?
    
    operator fun <V : Transformer> get(clazz: KClass<V>): V?
    
    fun <V : Transformer> getInfo(clazz: KClass<V>): TransformerInfo?
    
    fun iterator(): Iterator<T>
    
    fun infoIterator(): Iterator<TransformerInfo>
    
}

inline fun <reified K : Transformer> TransformerRegistry<out Transformer>.get() = get(K::class)

inline fun <reified K : Transformer> TransformerRegistry<out Transformer>.getInfo() = getInfo(K::class)