package xyz.xenondevs.vetric.transformer.registry

import it.unimi.dsi.fastutil.objects.Object2ObjectMaps
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
import xyz.xenondevs.bytebase.jvm.JavaArchive
import xyz.xenondevs.vetric.config.JsonConfig
import xyz.xenondevs.vetric.config.VetricConfig
import xyz.xenondevs.vetric.transformer.Transformer
import xyz.xenondevs.vetric.transformer.TransformerPriority
import kotlin.reflect.KClass

/**
 * Transformer that holds multiple transformers of a specific category. E.g. all flow obfuscators, all string
 * obfuscators, etc.
 */
abstract class SubTransformerRegistry<T : Transformer>(
    name: String,
    priority: TransformerPriority
) : Transformer(name, priority), TransformerRegistry<T> {
    
    private val registry = Object2ObjectMaps.synchronize(Object2ObjectOpenHashMap<String, TransformerInfo>())
    private var _enabled = Object2ObjectMaps.synchronize(Object2ObjectOpenHashMap<KClass<out T>, T>())
    
    val enabled: Collection<Transformer>
        get() = _enabled.values
    
    @Suppress("UNCHECKED_CAST")
    override fun loadConfig(config: JsonConfig, vetricConfig: VetricConfig) {
        infoIterator().forEach { info ->
            val subConfig = vetricConfig[this, info] ?: return@forEach
            if (!subConfig.getBoolean("enabled", default = true)) return@forEach
            
            val transformer = info.constructor(subConfig, vetricConfig) as T
            transformer.vetric = vetricConfig.vetric
            transformer.upperRegistry = this
            _enabled[info.clazz as KClass<T>?] = transformer
        }
    }
    
    fun register(name: String, clazz: KClass<out T>, constructor: (JsonConfig, VetricConfig) -> T) {
        synchronized(registry) {
            require(!registry.containsKey(name)) { "SubTransformer with name $name already registered (" + registry[name]!!.javaClass.canonicalName + ")" }
            
            registry[name] = TransformerInfo(name, clazz, constructor)
        }
    }
    
    inline fun <reified V : T> register(name: String, noinline constructor: () -> V) {
        register(name, V::class) { config, vetricConfig ->
            constructor().apply { loadConfig(config, vetricConfig) }
        }
    }
    
    override fun prepare(jar: JavaArchive) {
        enabled.forEach { it.prepare(jar) }
    }
    
    override fun transform(jar: JavaArchive) {
        enabled.forEach {
            logger.info("Applying " + it.name + "...")
            it.transform(jar)
        }
    }
    
    override fun get(name: String): T? =
        _enabled.values.firstOrNull { it.name == name }
    
    @Suppress("UNCHECKED_CAST")
    override fun <V : Transformer> get(clazz: KClass<V>): V? =
        _enabled[clazz as KClass<out T>] as V?
    
    
    override fun getInfo(name: String): TransformerInfo? =
        registry[name]
    
    override fun <V : Transformer> getInfo(clazz: KClass<V>): TransformerInfo? =
        registry.values.firstOrNull { it.clazz == clazz }
    
    override fun iterator(): Iterator<T> =
        _enabled.values.iterator()
    
    override fun infoIterator(): Iterator<TransformerInfo> =
        registry.values.iterator()
    
}