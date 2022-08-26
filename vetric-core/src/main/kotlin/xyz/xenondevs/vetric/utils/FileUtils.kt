package xyz.xenondevs.vetric.utils

import java.io.File
import java.io.LineNumberReader

val File.lineCount: Int
    get() {
        val reader = LineNumberReader(bufferedReader())
        reader.use { it.skip(Long.MAX_VALUE) }
        return reader.lineNumber
    }