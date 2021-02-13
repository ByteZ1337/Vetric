package xyz.xenondevs.vetric

import xyz.xenondevs.vetric.config.ObfuscatorConfig
import xyz.xenondevs.vetric.jvm.ClassPath
import xyz.xenondevs.vetric.jvm.JavaArchive
import xyz.xenondevs.vetric.jvm.Library
import xyz.xenondevs.vetric.transformer.TransformerRegistry
import xyz.xenondevs.vetric.util.json.JsonConfig
import java.io.File
import java.util.jar.JarOutputStream

object Vetric {
    
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
        val enabled = TransformerRegistry.getEnabled()
        require(enabled.isNotEmpty()) { "No transformers are enabled!" } // Might be removed
        
        enabled.forEach { it.runPreparations(input) }
        enabled.forEach { it.transformJar(input) }
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