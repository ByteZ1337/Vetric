package xyz.xenondevs.obfuscator.utils

import java.io.File
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import java.util.zip.ZipOutputStream

object FileUtils {
    
    const val ZIP_PREFIX = 0x504B0304u
    const val CLASS_PREFIX = 0xCAFEBABEu
    
    fun getFileExtension(path: String) = path.substringAfterLast('.')
    
}

fun File.startsWith(prefix: ByteArray): Boolean {
    if (this.length() < prefix.size)
        return false
    
    val array = ByteArray(prefix.size)
    this.inputStream().also { it.read(array); it.close() }
    return prefix.contentEquals(array)
}

fun ZipFile.readEntry(entry: ZipEntry): ByteArray {
    val inputStream = this.getInputStream(entry) ?: return ByteArray(0)
    val bytes = inputStream.readBytes()
    inputStream.close()
    return bytes
}

fun ZipOutputStream.writeAndClose(entry: ZipEntry, content: ByteArray) {
    this.putNextEntry(entry)
    this.write(content, 0, content.size)
    this.closeEntry()
    this.flush()
}
