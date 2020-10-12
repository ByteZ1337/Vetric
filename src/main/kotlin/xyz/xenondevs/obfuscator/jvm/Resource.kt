package xyz.xenondevs.obfuscator.jvm

import xyz.xenondevs.obfuscator.util.FileUtils

class Resource(var fileName: String, var content: ByteArray, val jar: JavaArchive? = null) {

    val originalName = fileName

    override fun toString() = content.decodeToString()

    fun getExtension() = FileUtils.getFileExtension(fileName)

    fun apply(consumer: (ByteArray) -> Unit) = consumer(content)

}