package xyz.xenondevs.obfuscator.util

import java.io.File
import java.lang.reflect.Method
import java.net.URL
import java.net.URLClassLoader

object FileUtils {

    val classLoader = ClassLoader.getSystemClassLoader() as URLClassLoader
    val method: Method = URLClassLoader::class.java.getDeclaredMethod("addURL", URL::class.java)

    init {
        method.isAccessible = true
    }

    fun getExtension(path: String): String {
        val index = path.lastIndexOf('.')
        return if (index < 0) "" else path.substring(index + 1)
    }

    fun loadLibrary(file: File) {
        method.invoke(classLoader, file.toURI().toURL())
    }
}