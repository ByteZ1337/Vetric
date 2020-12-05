package xyz.xenondevs.obfuscator.config.type

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import xyz.xenondevs.obfuscator.transformers.Transformer
import xyz.xenondevs.obfuscator.transformers.TransformerRegistry
import xyz.xenondevs.obfuscator.utils.json.getBoolean
import kotlin.reflect.KClass

open class TransformerType(transformerClass: KClass<out Transformer>) : SettingType<Unit>() {
    
    private val transformer by lazy {
        TransformerRegistry.transformers.first { transformerClass == it::class }
    }
    
    override fun isValid(element: JsonElement, silent: Boolean) = element is JsonObject
    
    open fun parse(obj: JsonObject) {
        transformer.enabled = obj.getBoolean("enabled", false)
    }
    
    override fun parseElement(element: JsonElement) = parse(element as JsonObject)
    
}