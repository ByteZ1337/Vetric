package xyz.xenondevs.vetric.transformer

import kotlin.reflect.KClass

class TransformerInfo(val name: String, val clazz: KClass<out Transformer>, val constructor: TransformerConstructor)