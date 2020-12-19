package xyz.xenondevs.vetric.transformers.renamer

import com.google.gson.JsonObject
import xyz.xenondevs.vetric.config.type.TransformerConfig
import xyz.xenondevs.vetric.jvm.JavaArchive
import xyz.xenondevs.vetric.transformers.Transformer
import xyz.xenondevs.vetric.transformers.TransformerPriority.HIGHEST
import xyz.xenondevs.vetric.utils.json.getBoolean

object Shuffler : Transformer("Shuffler", ShufflerConfig, HIGHEST) {
    
    var shuffleFields = true
    var shuffleMethods = true
    var crossClass = false
    
    override fun transformJar(jar: JavaArchive) {
        jar.classes.forEach {
            if (shuffleFields && !it.fields.isNullOrEmpty())
                it.fields.shuffle()
            if (shuffleMethods && !it.methods.isNullOrEmpty())
                it.methods.shuffle()
        }
    }
    
    object ShufflerConfig : TransformerConfig(Shuffler::class) {
        override fun parse(obj: JsonObject) {
            super.parse(obj)
            shuffleFields = obj.getBoolean("fields", true)
            shuffleMethods = obj.getBoolean("methods", true)
            crossClass = obj.getBoolean("crossclass", false)
        }
    }
    
}