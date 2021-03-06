package xyz.xenondevs.vetric.jvm

import xyz.xenondevs.vetric.util.FileUtils

class Resource(var fileName: String, var content: ByteArray) {
    
    val originalName = fileName
    
    override fun toString() = content.decodeToString()
    
    fun getExtension() = FileUtils.getFileExtension(fileName)
    
    fun apply(consumer: (ByteArray) -> Unit) = consumer(content)
    
}