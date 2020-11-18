package xyz.xenondevs.obfuscator

import xyz.xenondevs.obfuscator.config.ObfuscatorConfig
import xyz.xenondevs.obfuscator.jvm.ClassPath
import xyz.xenondevs.obfuscator.jvm.JavaArchive
import xyz.xenondevs.obfuscator.jvm.Library
import xyz.xenondevs.obfuscator.transformers.TransformerRegistry
import xyz.xenondevs.obfuscator.utils.json.JsonConfig
import java.io.File
import java.util.jar.JarOutputStream

object Obfuscator {
    
    lateinit var input: JavaArchive
    lateinit var output: File
    
    fun run() {
        initFiles(File("config.json"))
        applyTransformers()
        writeOutput()
    }
    
    fun initFiles(configFile: File) {
        ObfuscatorConfig.load(JsonConfig(configFile, true))
        input = JavaArchive(ObfuscatorConfig.input)
        output = ObfuscatorConfig.output
        ClassPath.reset()
        ClassPath.loadJar(input)
        ObfuscatorConfig.libraries.forEach(ClassPath::loadLibrary)
    }
    
    fun applyTransformers() {
        TransformerRegistry.transformers.forEach { it.transformJar(input) }
    }
    
    fun writeOutput() {
        println("Writing file...")
        val outputStream = JarOutputStream(output.outputStream())
        val writtenEntries = HashSet<String>()
        input.write(outputStream, false, writtenEntries)
        ClassPath.libraries.filter(Library::extract).forEach { it.write(outputStream, false, writtenEntries) }
        outputStream.close()
    }
    
}