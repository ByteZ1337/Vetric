package xyz.xenondevs.vetric.config

import com.google.gson.JsonObject
import xyz.xenondevs.vetric.Vetric
import xyz.xenondevs.vetric.jvm.Library
import xyz.xenondevs.vetric.transformer.Transformer
import xyz.xenondevs.vetric.transformer.registry.DefaultTransformerRegistry
import xyz.xenondevs.vetric.transformer.registry.TransformerInfo
import java.io.File

class VetricConfig(supplier: ConfigSupplier, val vetric: Vetric) : JsonConfig(supplier, autoInit = true) {
    
    var isDebug: Boolean = false
    
    var input: File = this["input"] ?: throw IllegalStateException("Input file not set")
    var output: File = this["output"] ?: throw IllegalStateException("Output file not set")
    var libraries: List<Library> = this["libraries"] ?: emptyList()
    val registry = DefaultTransformerRegistry()
    
    init {
        if (getElement("transformers") !is JsonObject) {
            vetric.logger.warn("No transformers found in config")
        } else {
            registry.loadConfig(this)
            if (registry.enabled.isEmpty())
                vetric.logger.warn("NO TRANSFORMERS ENABLED")
        }
    }
    
    operator fun get(transformer: TransformerInfo): JsonConfig? {
        val transformers = getElement("transformers") as JsonObject
        val name = transformer.name
        val configName = transformers.keySet().firstOrNull { name.equals(it, true) } ?: return null
        val config = getElement("transformers.$configName")!!
        check(config is JsonObject) { "Transformer config for $transformer must be a JsonObject" }
        return JsonConfig(config)
    }
    
    operator fun get(subRegistry: Transformer, transformer: TransformerInfo): JsonConfig? {
        val transformers = getElement("transformers") as JsonObject
        val parentName = subRegistry.name
        val name = transformer.name
        
        var configName = transformers.keySet().firstOrNull { parentName.equals(it, ignoreCase = true) } ?: return null
        val parentObject = getElement("transformers.$configName.transformers") ?: return null
        check(parentObject is JsonObject) { "Transformers config for $parentName must be a JsonObject" }
        
        configName = parentObject.keySet().firstOrNull { name.equals(it, ignoreCase = true) } ?: return null
        val config = parentObject.get(configName)!!
        check(config is JsonObject) { "Transformer config for $name must be a JsonObject" }
        
        return JsonConfig(config)
    }
    
}