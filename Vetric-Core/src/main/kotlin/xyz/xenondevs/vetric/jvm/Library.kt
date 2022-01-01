package xyz.xenondevs.vetric.jvm

import org.objectweb.asm.ClassReader.SKIP_CODE
import xyz.xenondevs.bytebase.jvm.JavaArchive
import java.io.File

class Library(file: File, val isExtracted: Boolean) : JavaArchive(file, if (isExtracted) 0 else SKIP_CODE) {
    
    fun extractInto(jar: JavaArchive) {
        directories.filterNot(jar.directories::contains).forEach(jar.directories::add)
        classes.filterNot(jar.classes::contains).forEach(jar.classes::add)
        resources.filterNot(jar.resources::contains).forEach(jar.resources::add)
    }
    
}