package xyz.xenondevs.vetric.transformer.registry

import xyz.xenondevs.vetric.transformer.Transformer
import kotlin.reflect.KClass

class TransformerInfo(val name: String, val clazz: KClass<out Transformer>, val constructor: TransformerConstructor)