package xyz.xenondevs.obfuscator.jvm

import java.io.File

class Library(file: File, val extract: Boolean) : JavaArchive(file)