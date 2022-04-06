package xyz.xenondevs.vetric.transformer

import xyz.xenondevs.bytebase.jvm.JavaArchive
import xyz.xenondevs.vetric.config.JsonConfig
import xyz.xenondevs.vetric.config.VetricConfig
import xyz.xenondevs.vetric.logging.info

/**
 * Transformer that holds multiple transformers of a specific category. E.g. all flow obfuscators, all string
 * obfuscators, etc.
 */
abstract class SubTransformerRegistry<T : Transformer>(
    name: String,
    priority: TransformerPriority
) : Transformer(name, priority) {
    
    abstract val transformers: List<T>
    protected var enabledTransformers = mutableListOf<T>()
    
    override fun loadConfig(config: JsonConfig, vetricConfig: VetricConfig) {
        transformers.forEach { transformer ->
            val subConfig = vetricConfig[this, transformer] ?: return@forEach
            val enabled = subConfig.getBoolean("enabled", default = true)
            if (enabled) {
                transformer.loadConfig(subConfig, vetricConfig)
                enabledTransformers += transformer
            }
        }
    }
    
    override fun transform(jar: JavaArchive) {
        enabledTransformers.forEach {
            info("Applying " + it.name + "...")
            it.transform(jar)
        }
    }
    
    override fun prepare(jar: JavaArchive) {
        enabledTransformers.forEach { it.prepare(jar) }
    }
    
    
}