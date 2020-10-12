package xyz.xenondevs.obfuscator.jvm

import xyz.xenondevs.obfuscator.util.*
import xyz.xenondevs.obfuscator.util.FileUtils.CLASS_PREFIX
import xyz.xenondevs.obfuscator.util.FileUtils.ZIP_PREFIX
import java.io.File
import java.io.OutputStream
import java.util.jar.JarEntry
import java.util.jar.JarFile
import java.util.jar.JarOutputStream

class JavaArchive() {

    val directories = ArrayList<JarEntry>()
    val classes = ArrayList<ClassWrapper>()
    val resources = ArrayList<Resource>()

    constructor(file: File, loadAsLibrary: Boolean = false) : this() {
        readFile(file)
        if (loadAsLibrary)
            FileUtils.loadLibrary(file, this)
    }

    private fun readFile(file: File) {
        if(!file.exists())
            error("The given file doesn't exist.")

        // If the file doesn't have the proper format throw an exception
        if (!file.startsWith(ZIP_PREFIX.toByteArray()))
            error("The given file is not a Java archive.")

        val jar = JarFile(file)
        jar.stream().forEach { entry ->
            if (entry.isDirectory) {
                directories += entry
                return@forEach
            }
            val name = entry.name
            val content = jar.readEntry(entry)
            // Check if the filetype is class.
            // IgnoreCase shouldn't be used since a compiler will always compile to lowercase .class
            if ("class" == FileUtils.getFileExtension(name)
                // Check if the file starts with 0xCAFEBABE
                && content.take(4).toByteArray().contentEquals(CLASS_PREFIX.toByteArray())
            ) classes += ClassWrapper(name, content, this)
            else resources += Resource(name, content, this)
        }
    }

    fun writeFile(file: File) = write(file.outputStream())

    fun write(outputStream: OutputStream) {
        val jos = if (outputStream is JarOutputStream) outputStream else JarOutputStream(outputStream)
        // Writing directories
        directories.forEach {
            it.time = System.currentTimeMillis()
            jos.putNextEntry(it)
            jos.closeEntry()
        }
        // Writing resources
        resources.forEach {
            val je = JarEntry(it.fileName)
            je.time = System.currentTimeMillis()
            jos.writeAndClose(je, it.content)
        }
        // Writing classes
        classes.forEach {
            val je = JarEntry(it.fileName)
            je.time = System.currentTimeMillis()
            jos.writeAndClose(je, it.getByteCode())
        }
        jos.close()
    }

    fun getClass(name: String) = classes.firstOrNull {
        it.name == name || (name.contains(".class") && it.fileName == name)
    }

    fun getResource(name: String) =
        resources.firstOrNull { it.fileName == name }

    infix operator fun plusAssign(clazz: ClassWrapper) {
        classes += clazz
    }

    infix operator fun plusAssign(resource: Resource) {
        resources += resource
    }

    infix operator fun minusAssign(clazz: ClassWrapper) {
        classes -= clazz
    }

    infix operator fun minusAssign(resource: Resource) {
        resources -= resource
    }

}