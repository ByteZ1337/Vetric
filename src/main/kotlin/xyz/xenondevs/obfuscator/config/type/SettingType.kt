package xyz.xenondevs.obfuscator.config.type

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import xyz.xenondevs.obfuscator.utils.json.JsonConfig

abstract class SettingType<T> {
    
    abstract fun isValid(element: JsonElement, silent: Boolean = false): Boolean
    
    fun isValid(path: String, config: JsonConfig, silent: Boolean = false) =
        config[path]?.let { isValid(it, silent) } ?: false
    
    internal abstract fun parseElement(element: JsonElement): T
    
    fun parse(element: JsonElement): T? =
        if (!isValid(element)) null else parseElement(element)
    
    fun parse(path: String, config: JsonConfig) = config[path]?.let { parse(it) }
    
    fun parseList(array: JsonArray) = array.filter(this::isValid).map(this::parse)
    
}