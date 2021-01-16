package xyz.xenondevs.vetric.jvm

import org.objectweb.asm.ClassReader.SKIP_CODE
import org.objectweb.asm.ClassReader.SKIP_FRAMES
import java.io.File

class Library(file: File, val extract: Boolean) : JavaArchive(file, if (extract) SKIP_FRAMES else SKIP_CODE)