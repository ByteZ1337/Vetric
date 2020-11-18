package xyz.xenondevs.obfuscator.jvm

import xyz.xenondevs.obfuscator.utils.*
import xyz.xenondevs.obfuscator.utils.FileUtils.CLASS_PREFIX
import xyz.xenondevs.obfuscator.utils.FileUtils.ZIP_PREFIX
import java.io.File
import java.io.OutputStream
import java.util.jar.JarEntry
import java.util.jar.JarFile
import java.util.jar.JarOutputStream
import java.util.zip.ZipOutputStream

open class JavaArchive() {

    val directories = ArrayList<JarEntry>()
    val classes = ArrayList<ClassWrapper>()
    val resources = ArrayList<Resource>()

    constructor(file: File) : this() {
        readFile(file)
    }

    private fun readFile(file: File) {
        require(file.exists()) { "The given file doesn't exist" }
        // If the file doesn't have the proper format throw an exception
        require(file.startsWith(ZIP_PREFIX.toByteArray())) { "The given file is not a Java archive." }

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
            ) classes += ClassWrapper(name, content)
            else resources += Resource(name, content)
        }
    }

    fun writeFile(file: File) = write(file.outputStream())

    fun write(outputStream: OutputStream, close: Boolean = true, writtenEntries: HashSet<String> = HashSet()) {
        val jos = if (outputStream is ZipOutputStream) outputStream else JarOutputStream(outputStream)
        // Writing directories
        directories.forEach {
            if (writtenEntries.contains(it.name))
                return@forEach
            it.time = System.currentTimeMillis()
            jos.putNextEntry(it)
            jos.closeEntry()
            writtenEntries += it.name
        }
        // Writing resources
        resources.forEach {
            if (writtenEntries.contains(it.fileName))
                return@forEach
            val je = JarEntry(it.fileName)
            je.time = System.currentTimeMillis()
            jos.writeAndClose(je, it.content)
            writtenEntries += it.fileName
        }
        // Writing classes
        classes.forEach {
            if (writtenEntries.contains(it.fileName))
                return@forEach
            val je = JarEntry(it.fileName)
            je.time = System.currentTimeMillis()
            jos.writeAndClose(je, it.getByteCode())
            writtenEntries += je.name
        }
        if (close)
            jos.close()
    }

    fun getClass(name: String) = classes.firstOrNull {
        it.name == name || (name.contains(".class") && it.fileName == name)
    }

    fun getResource(name: String) = resources.firstOrNull { it.fileName == name }

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