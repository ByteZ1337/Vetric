package xyz.xenondevs.obfuscator.util

object FileUtils {

    fun getExtenstion(path: String): String {
        val index = path.lastIndexOf('.')
        return if (index < 0) "" else path.substring(index + 1)
    }


}