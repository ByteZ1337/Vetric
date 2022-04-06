package xyz.xenondevs.vetric.config

import com.google.gson.JsonObject
import xyz.xenondevs.vetric.jvm.Library
import xyz.xenondevs.vetric.logging.warn
import xyz.xenondevs.vetric.transformer.Transformer
import xyz.xenondevs.vetric.transformer.TransformerRegistry
import xyz.xenondevs.vetric.utils.getBoolean
import xyz.xenondevs.vetric.utils.isBoolean
import java.io.File

class VetricConfig(supplier: ConfigSupplier) : JsonConfig(supplier, autoInit = true) {
    
    var input: File = this["input"] ?: throw IllegalStateException("Input file not set")
    var output: File = this["output"] ?: throw IllegalStateException("Output file not set")
    val libraries: List<Library> = this["libraries"] ?: emptyList()
    var transformers = emptyList<Transformer>()
        private set
    
    init {
        val obj = getElement("transformers")
        if (obj is JsonObject) {
            val keys = obj.keySet()
            transformers = TransformerRegistry.filter { transformer ->
                val key = transformer.name
                val configKey = keys.firstOrNull { key.equals(it, ignoreCase = true) } ?: return@filter false
                val config = obj[configKey]
                return@filter (config.isBoolean() && config.asBoolean) || (config is JsonObject && config.getBoolean("enabled", default = true))
            }
        }
        if (transformers.isEmpty())
            warn("NO TRANSFORMERS ENABLED")
    }
    
    operator fun get(transformer: Transformer): JsonConfig {
        val obj = getElement("transformers") as JsonObject
        val configName = obj.keySet().firstOrNull { transformer.name.equals(it, true) }!! // Can't be null because of the check above
        val config = getElement("transformers.$configName")!!
        check(config is JsonObject) { "Transformer config must be a JsonObject" }
        return JsonConfig(config)
    }
    
}