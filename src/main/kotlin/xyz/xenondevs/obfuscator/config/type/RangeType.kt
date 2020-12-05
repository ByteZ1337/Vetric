package xyz.xenondevs.obfuscator.config.type

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import xyz.xenondevs.obfuscator.utils.json.getInt
import xyz.xenondevs.obfuscator.utils.json.hasNumber
import xyz.xenondevs.obfuscator.utils.json.isNumber

object RangeType : SettingType<Pair<Int, Int>>() {
    
    override fun isValid(element: JsonElement, silent: Boolean): Boolean {
        if (element.isNumber())
            return true
        if (element is JsonObject) {
            if (!element.hasNumber("min")) {
                require(silent) { "Missing min property for range." }
                return false
            }
            if (!element.hasNumber("max")) {
                require(silent) { "Missing max property for range." }
                return false
            }
            if (element.getInt("min")!! > element.getInt("max")!!) {
                require(silent) { "min must not be greater than max." }
                return false
            }
            
            return true
        }
        
        require(silent) { "Invalid element for range." }
        return false
    }
    
    override fun parseElement(element: JsonElement): Pair<Int, Int> {
        if (element is JsonPrimitive)
            return element.asInt to element.asInt
        
        element as JsonObject
        return element.getInt("min")!! to element.getInt("max")!!
    }
}