package xyz.xenondevs.obfuscator.transformer

import xyz.xenondevs.obfuscator.transformer.number.ArithmeticConverter
import xyz.xenondevs.obfuscator.transformer.number.LogicalConverter
import xyz.xenondevs.obfuscator.transformer.renamer.ClassRenamer
import xyz.xenondevs.obfuscator.transformer.renamer.Cleaner
import xyz.xenondevs.obfuscator.transformer.renamer.FieldRenamer
import xyz.xenondevs.obfuscator.transformer.renamer.UpdateResourceContents
import xyz.xenondevs.obfuscator.transformer.string.EncryptionInjector
import xyz.xenondevs.obfuscator.transformer.string.StringEncrypter

@ExperimentalStdlibApi
class TransformerRegistry {
    var transformers = ArrayList<Transformer>()

    init {
        transformers.add(EncryptionInjector())
        transformers.add(StringEncrypter())
        transformers.add(LogicalConverter())
        transformers.add(ArithmeticConverter())
        transformers.add(Cleaner())
//        transformers.add(LocalRenamer())
        transformers.add(FieldRenamer())
        transformers.add(ClassRenamer())
        transformers.add(UpdateResourceContents())
    }

    fun <T> getTransformer(clazz: Class<T>): Transformer? =
            transformers.firstOrNull { it::class.java == clazz }

}