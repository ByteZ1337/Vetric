package xyz.xenondevs.obfuscator.asm

import xyz.xenondevs.obfuscator.transformer.Transformer
import xyz.xenondevs.obfuscator.util.FileUtils
import java.io.File
import java.io.IOException
import java.net.URL
import java.net.URLClassLoader
import java.util.jar.JarEntry
import java.util.jar.JarFile
import java.util.jar.JarOutputStream

@ExperimentalStdlibApi
class SmartJar {
    var files = HashMap<String, ByteArray>()
    var directories = HashMap<String, JarEntry>()
    var resources = ArrayList<Resource>()
    var classes = ArrayList<SmartClass>()

    @Throws(IOException::class)
    fun readFile(file: File) {
        println("Reading jar... (${file.absolutePath})")
        val jarFile = JarFile(file)
        jarFile.stream().forEach {
            run {
                if (it.isDirectory) {
                    directories[it.name] = it
                    return@forEach
                }
                val inputStream = jarFile.getInputStream(it)
                files[it.name] = inputStream.readBytes()
                inputStream.close()
            }
        }
        parseFiles()
        println("${classes.size} Classes, ${resources.size} Resources, ${directories.size} Directories, Total: ${files.size + directories.size}")
        // Loading Jar as library
        val classLoader = ClassLoader.getSystemClassLoader() as URLClassLoader
        val method = URLClassLoader::class.java.getDeclaredMethod("addURL", URL::class.java)
        method.isAccessible = true
        method.invoke(classLoader, file.toURI().toURL())
    }

    fun writeFile(file: File) {
        println("----------------------")
        println("Writing jar (${file.absolutePath})")
        val jos = JarOutputStream(file.outputStream())
        files.forEach { (name, content) ->
            run {
                val je = JarEntry(name)
                je.time = System.currentTimeMillis()
                jos.putNextEntry(je)
                jos.write(content, 0, content.size)
                jos.closeEntry()
            }
        }
        jos.flush()
        jos.close()
        println("Done! Size: ${file.length()} bytes")
    }

    private fun parseFiles() {
        files.forEach { (name, content) ->
            run {
                if (FileUtils.getExtenstion(name) != "class")
                    resources.add(Resource(name, content, this))
                else classes.add(SmartClass(name, content, this))
            }
        }
    }

    fun apply(transformer: Transformer) {
        println("----------------------")
        println("Running ${transformer.name}...")
        println()
        transformer.transform(this)
    }

    fun findClass(name: String): SmartClass = classes.stream().filter { it.fileName == name }.findFirst().orElse(null)

}