package xyz.xenondevs.vetric.serialization

import com.google.gson.*
import xyz.xenondevs.vetric.supplier.CharSupplierConfig
import xyz.xenondevs.vetric.supplier.DictionarySupplierConfig
import xyz.xenondevs.vetric.supplier.NormalSupplierConfig
import xyz.xenondevs.vetric.supplier.SupplierFactory
import xyz.xenondevs.vetric.supplier.registry.SupplierRegistry
import xyz.xenondevs.vetric.supplier.registry.SupplierType.*
import xyz.xenondevs.vetric.utils.hasNumber
import xyz.xenondevs.vetric.utils.hasString
import xyz.xenondevs.vetric.utils.isString
import java.io.File
import java.lang.reflect.Type

object SupplierFactorySerialization : JsonSerializer<SupplierFactory>, JsonDeserializer<SupplierFactory> {
    
    override fun serialize(factory: SupplierFactory, type: Type, ctx: JsonSerializationContext): JsonElement {
        TODO()
    }
    
    override fun deserialize(element: JsonElement, type: Type, ctx: JsonDeserializationContext): SupplierFactory {
        // If the element is simply a string, then we can just return the supplier with the given name and default settings.
        if (element.isString()) {
            val name = element.asString
            val info = SupplierRegistry.getSupplier(name) ?: throw JsonParseException("Supplier $name not found")
            return SupplierFactory(info, info.type.defaultConfig)
        }
        
        // Otherwise, we need to parse the element as a JsonObject.
        if (element is JsonObject) {
            if (element.hasString("name")) {
                val name = element.get("name").asString
                val info = SupplierRegistry.getSupplier(name) ?: throw JsonParseException("Supplier $name not found")
                val config = when (info.type) {
                    NORMAL -> deserializeNormalConfig(element)
                    CHAR -> deserializeCharConfig(element)
                    DICTIONARY -> deserializeDictionaryConfig(element)
                }
                return SupplierFactory(info, config)
            } else {
                if (!element.hasString("path"))
                    failParse("Missing path for custom dictionary.")
                val file = File(element.get("path").asString)
                if (!file.exists())
                    failParse("File ${file.absolutePath} does not exist.")
                return SupplierFactory(file, deserializeDictionaryConfig(element))
            }
        }
        
        failParse("Invalid Json type. Expected String or JsonObject. Got ${element.javaClass.simpleName}")
    }
    
    private fun failParse(extra: String): Nothing = throw JsonParseException("Invalid supplier factory: $extra")
    
    private fun deserializeNormalConfig(obj: JsonObject): NormalSupplierConfig {
        val (min, max) = getMinMax(obj)
        
        return NormalSupplierConfig(min, max)
    }
    
    private fun deserializeCharConfig(obj: JsonObject): CharSupplierConfig {
        val (min, max) = getMinMax(obj)
        
        
        val countUp = obj.get("countup")?.asBoolean
            // If no length is explicitly set, set countUp to true
            ?: !obj.hasNumber("length") && !obj.hasNumber("minlength") && !obj.hasNumber("maxlength")
        
        return CharSupplierConfig(min, max, countUp)
    }
    
    private fun getMinMax(obj: JsonObject): Pair<Int, Int> {
        
        val min: Int
        val max: Int
        
        if (obj.hasNumber("length")) {
            min = obj.get("length").asInt
            max = min
        } else {
            min = (obj.get("minlength")?.asInt ?: 1)
                .coerceAtLeast(1)
            max = (obj.get("maxlength")?.asInt ?: 20)
                .coerceAtLeast(min)
        }
        
        return min to max
    }
    
    private fun deserializeDictionaryConfig(obj: JsonObject): DictionarySupplierConfig {
        val countUp = obj.get("countup")?.asBoolean ?: true
        
        return DictionarySupplierConfig(countUp)
    }
    
}