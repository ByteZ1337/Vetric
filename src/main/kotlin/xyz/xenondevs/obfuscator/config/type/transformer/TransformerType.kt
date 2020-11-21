package xyz.xenondevs.obfuscator.config.type.transformer

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import xyz.xenondevs.obfuscator.config.type.SettingType
import xyz.xenondevs.obfuscator.transformers.Transformer
import xyz.xenondevs.obfuscator.transformers.TransformerRegistry
import xyz.xenondevs.obfuscator.utils.json.getBoolean

open class TransformerType(val transformer: Transformer) : SettingType<Unit>() {
    
    override fun isValid(element: JsonElement, silent: Boolean) = element is JsonObject
    
    override fun parseElement(element: JsonElement) {
        if (isValid(element)) parse(element)
    }
    
    open fun parse(obj: JsonObject) {
        if (!obj.getBoolean("enabled", true))
            TransformerRegistry.transformers.removeIf { it == transformer }
    }
    
}