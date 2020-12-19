package xyz.xenondevs.vetric.config.type

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import xyz.xenondevs.vetric.transformers.Transformer
import xyz.xenondevs.vetric.transformers.TransformerRegistry
import xyz.xenondevs.vetric.utils.json.getBoolean
import kotlin.reflect.KClass

open class TransformerConfig(transformerClass: KClass<out Transformer>) : SettingType<Unit>() {
    
    private val transformer by lazy {
        TransformerRegistry.transformers.first { transformerClass == it::class }
    }
    
    override fun isValid(element: JsonElement, silent: Boolean) = element is JsonObject
    
    open fun parse(obj: JsonObject) {
        transformer.enabled = obj.getBoolean("enabled", false)
    }
    
    override fun parseElement(element: JsonElement) = parse(element as JsonObject)
    
}