package xyz.xenondevs.vetric.serialization

import com.google.gson.*
import xyz.xenondevs.vetric.supplier.SupplierConfig
import xyz.xenondevs.vetric.utils.hasNumber
import java.lang.reflect.Type

object SupplierConfigSerialization : JsonSerializer<SupplierConfig>, JsonDeserializer<SupplierConfig> {
    
    override fun serialize(config: SupplierConfig, type: Type, ctx: JsonSerializationContext): JsonElement {
        val obj = JsonObject()
        if (config.max == config.min) {
            obj.addProperty("length", config.min)
        } else {
            obj.addProperty("minlength", config.min)
            obj.addProperty("maxlength", config.max)
        }
        if (config.countUp != null)
            obj.addProperty("countup", config.countUp)
        return obj
    }
    
    override fun deserialize(element: JsonElement, type: Type, ctx: JsonDeserializationContext): SupplierConfig {
        if (element !is JsonObject)
            throw JsonParseException("Expected JsonObject")
        
        val min: Int
        val max: Int
        
        if (element.hasNumber("length")) {
            min = element.get("length").asInt
            max = min
        } else {
            min = (element.get("minlength")?.asInt ?: 1)
                .coerceAtLeast(1)
            max = (element.get("maxlength")?.asInt ?: 20)
                .coerceAtLeast(min)
        }
        
        val countUp = element.get("countup")?.asBoolean
            ?: !element.hasNumber("length") && !element.hasNumber("minlength") && !element.hasNumber("maxlength")
        
        return SupplierConfig(min, max, countUp)
    }
    
}