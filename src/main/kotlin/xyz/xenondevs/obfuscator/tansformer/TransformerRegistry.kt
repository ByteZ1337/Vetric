package xyz.xenondevs.obfuscator.tansformer

import xyz.xenondevs.obfuscator.tansformer.number.ArithmeticConverter
import xyz.xenondevs.obfuscator.tansformer.number.LogicalConverter
import xyz.xenondevs.obfuscator.tansformer.renamer.ClassRenamer
import xyz.xenondevs.obfuscator.tansformer.renamer.Cleaner
import xyz.xenondevs.obfuscator.tansformer.renamer.FieldRenamer
import xyz.xenondevs.obfuscator.tansformer.renamer.UpdateResourceContents
import xyz.xenondevs.obfuscator.tansformer.string.EncryptionInjector
import xyz.xenondevs.obfuscator.tansformer.string.StringEncrypter

@ExperimentalStdlibApi
class TransformerRegistry {
    var transformers = ArrayList<Transformer>()

    init {
        transformers.add(EncryptionInjector())
        transformers.add(StringEncrypter())
        transformers.add(LogicalConverter())
        transformers.add(ArithmeticConverter())
        transformers.add(Cleaner())
        //transformers.add(LocalRenamer())
        transformers.add(FieldRenamer())
        transformers.add(ClassRenamer())
        transformers.add(UpdateResourceContents())
    }

    fun <T> getTransformer(clazz: Class<T>): Transformer? =
            transformers.firstOrNull { it::class.java == clazz }

}