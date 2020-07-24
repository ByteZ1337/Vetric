package xyz.xenondevs.obfuscator.transformer

import xyz.xenondevs.obfuscator.transformer.misc.Cleaner
import xyz.xenondevs.obfuscator.transformer.number.ArithmeticConverter
import xyz.xenondevs.obfuscator.transformer.number.LogicalConverter
import xyz.xenondevs.obfuscator.transformer.renamer.*
import xyz.xenondevs.obfuscator.transformer.string.EncryptionInjector
import xyz.xenondevs.obfuscator.transformer.string.StringEncrypter

@ExperimentalStdlibApi
class TransformerRegistry {
    var transformers = ArrayList<Transformer>()

    init {
        addTransformers(
                EncryptionInjector(), StringEncrypter(),
                LogicalConverter(), ArithmeticConverter(),
                Cleaner(), MethodRenamer(), FieldRenamer(),
                ClassRenamer(), UpdateResourceContents()
        )
    }

    private fun addTransformers(vararg transformer: Transformer) {
        transformers.addAll(transformer)
    }

    inline fun <reified T> getTransformer(): T =
            transformers.firstOrNull { it::class.java == T::class.java } as T

}