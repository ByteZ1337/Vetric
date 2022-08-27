package xyz.xenondevs.vetric.transformer

import it.unimi.dsi.fastutil.objects.Object2ObjectMaps
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
import xyz.xenondevs.vetric.config.JsonConfig
import xyz.xenondevs.vetric.config.VetricConfig
import xyz.xenondevs.vetric.transformer.impl.obfuscation.misc.CodeHider
import xyz.xenondevs.vetric.transformer.impl.obfuscation.misc.kotlin.KotlinIntrinsicsReplacer
import xyz.xenondevs.vetric.transformer.impl.obfuscation.renamer.Renamer
import xyz.xenondevs.vetric.transformer.impl.obfuscation.renamer.ResourceUpdater
import xyz.xenondevs.vetric.transformer.impl.obfuscation.string.StringObfuscator
import xyz.xenondevs.vetric.transformer.impl.shrinking.LineNumberRemover
import kotlin.reflect.KClass

typealias TransformerConstructor = (JsonConfig, VetricConfig) -> Transformer
typealias EmptyTransformerConstructor = () -> Transformer

class DefaultTransformerRegistry(includeDefault: Boolean = true) : TransformerRegistry<Transformer> {
    
    private val registry = Object2ObjectMaps.synchronize(Object2ObjectOpenHashMap<String, TransformerInfo>())
    private var _enabled = Object2ObjectMaps.synchronize(Object2ObjectOpenHashMap<KClass<out Transformer>, Transformer>())
    
    val enabled: Collection<Transformer>
        get() = _enabled.values
    
    fun loadConfig(vetricConfig: VetricConfig) {
        infoIterator().forEach { info ->
            val config = vetricConfig[info] ?: return@forEach
            if(!config.getBoolean("enabled", default = true)) return@forEach
            
            val transformer = info.constructor(config, vetricConfig)
            transformer.vetric = vetricConfig.vetric
            transformer.upperRegistry = this
            _enabled[info.clazz] = transformer
        }
    }
    
    // <editor-fold desc="Default transformers" defaultstate="collapsed">
    
    init {
        if (includeDefault)
            registerDefault()
    }
    
    private fun registerDefault() {
        register("LineNumberRemover", ::LineNumberRemover)
        register("CodeHider", ::CodeHider)
        register("Renamer", ::Renamer)
        register("ResourceUpdater", ::ResourceUpdater)
        register("StringObfuscator", ::StringObfuscator)
        register("KotlinIntrinsicsReplacer", ::KotlinIntrinsicsReplacer)
    }
    
    // </editor-fold>
    
    fun register(name: String, clazz: KClass<out Transformer>, constructor: TransformerConstructor) {
        require(!registry.containsKey(name)) { "Transformer with name $name already registered (" + registry[name]!!.javaClass.canonicalName + ")" }
        
        registry[name] = TransformerInfo(name, clazz, constructor)
    }
    
    inline fun <reified T : Transformer> register(name: String, noinline constructor: () -> T) {
        register(name, T::class) { config, vetricConfig ->
            constructor().apply { loadConfig(config, vetricConfig) }
        }
    }
    
    override fun get(name: String): Transformer? =
        _enabled.values.firstOrNull { it.name == name }
    
    @Suppress("UNCHECKED_CAST")
    override fun <V : Transformer> get(clazz: KClass<V>): V? =
        _enabled[clazz] as V?
    
    
    override fun getInfo(name: String): TransformerInfo? =
        registry[name]
    
    override fun <V : Transformer> getInfo(clazz: KClass<V>): TransformerInfo? =
        registry.values.firstOrNull { it.clazz == clazz }
    
    override fun iterator(): Iterator<Transformer> =
        _enabled.values.iterator()
    
    override fun infoIterator(): Iterator<TransformerInfo> =
        registry.values.iterator()
    
}
