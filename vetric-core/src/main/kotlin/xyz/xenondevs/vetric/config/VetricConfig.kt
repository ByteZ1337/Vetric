package xyz.xenondevs.vetric.config

import com.google.gson.JsonObject
import xyz.xenondevs.vetric.jvm.Library
import xyz.xenondevs.vetric.logging.warn
import xyz.xenondevs.vetric.transformer.Transformer
import xyz.xenondevs.vetric.transformer.TransformerRegistry
import java.io.File

class VetricConfig(supplier: ConfigSupplier) : JsonConfig(supplier, autoInit = true) {
    
    var input: File = this["input"] ?: throw IllegalStateException("Input file not set")
    var output: File = this["output"] ?: throw IllegalStateException("Output file not set")
    var libraries: List<Library> = this["libraries"] ?: emptyList()
    var transformers = emptyList<Transformer>()
        private set
    
    init {
        val obj = getElement("transformers")
        if (obj is JsonObject) {
            transformers = TransformerRegistry.filter { transformer ->
                val config = this[transformer] ?: return@filter false
                val enabled = config.getBoolean("enabled", default = true)
                if (enabled) transformer.loadConfig(config, this)
                return@filter enabled
            }
        }
        if (transformers.isEmpty())
            warn("NO TRANSFORMERS ENABLED")
    }
    
    operator fun get(transformer: Transformer): JsonConfig? {
        val transformers = getElement("transformers") as JsonObject
        val name = transformer.name
        val configName = transformers.keySet().firstOrNull { name.equals(it, true) } ?: return null
        val config = getElement("transformers.$configName")!!
        check(config is JsonObject) { "Transformer config for $transformer must be a JsonObject" }
        return JsonConfig(config)
    }
    
    operator fun get(parentTransformer: Transformer, transformer: Transformer): JsonConfig? {
        val transformers = getElement("transformers") as JsonObject
        val parentName = parentTransformer.name
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