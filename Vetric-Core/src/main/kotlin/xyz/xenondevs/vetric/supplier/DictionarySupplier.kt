package xyz.xenondevs.vetric.supplier

import xyz.xenondevs.vetric.utils.lineCount
import java.io.BufferedReader
import java.io.File
import java.io.InputStream
import java.io.RandomAccessFile
import kotlin.math.min

class DictionarySupplier : StringSupplier {
    
    private val dictionary = ArrayList<String>()
    private var index = 0
    private var uniqueIndex = 0
    
    constructor(strings: List<String>, name: String, countUp: Boolean, needed: Int) : super(name) {
        if (strings.size < needed)
            throw IllegalArgumentException("Not enough strings in dictionary $name")
        
        dictionary.addAll((if (!countUp) strings.shuffled() else strings).take(needed))
    }
    
    constructor(inputStream: InputStream, name: String, countUp: Boolean, needed: Int)
        : this(inputStream.bufferedReader().use(BufferedReader::readLines), name, countUp, needed)
    
    constructor(file: File, countUp: Boolean, needed: Int) : super(file.nameWithoutExtension) {
        val lineCount = file.lineCount
        if (lineCount < needed)
            throw IllegalArgumentException("Not enough strings in dictionary $name")
        // For some special cases: supplier returns "getA" but a superclass already has "getA" which would lead to unwanted inheritance
        val actuallyNeeded = min(needed + 50, lineCount)
        
        val raf = RandomAccessFile(file, "r")
        val length = raf.length()
        do {
            if (!countUp) {
                val randomLocation = (Math.random() * length).toLong()
                raf.seek(randomLocation)
                raf.readLine()
            }
            if (raf.filePointer != length) {
                val name = raf.readLine()
                if (name !in dictionary)
                    dictionary += name
            }
        } while (dictionary.size < actuallyNeeded)
    }
    
    override fun randomString(): String {
        val string = dictionary[index++]
        if (index >= dictionary.size)
            index = 0
        return string
    }
    
    override fun randomStringUnique(exclude: HashSet<String>): String {
        if (uniqueIndex >= dictionary.size)
            throw IllegalStateException("$name ran out of unique strings. This should only happen on very rare occasions. You can try again or use a different dictionary.")
        
        return dictionary[uniqueIndex++]
    }
    
    override fun randomString(length: Int) = randomString()
    
    override fun randomStringUnique(length: Int, exclude: HashSet<String>) = randomStringUnique(exclude)
    
}