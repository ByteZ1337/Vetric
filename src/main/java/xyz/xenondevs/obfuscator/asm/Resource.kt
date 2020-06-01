package xyz.xenondevs.obfuscator.asm

import xyz.xenondevs.obfuscator.util.FileUtils
import java.util.function.Consumer

@ExperimentalStdlibApi
class Resource(var name: String, var content: ByteArray, val jar: SmartJar) {

    val originalName = name

    override fun toString(): String = content.decodeToString()

    fun getExtension(): String = FileUtils.getExtenstion(name)

    fun apply(consumer: Consumer<ByteArray>) = consumer.accept(this.content)

    fun update() {
        jar.files.remove(originalName);
        jar.files.remove(name);
        jar.files[name] = content
    }
}