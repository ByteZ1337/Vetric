package xyz.xenondevs.obfuscator.config

import xyz.xenondevs.obfuscator.jvm.Library
import xyz.xenondevs.obfuscator.utils.json.JsonConfig
import java.io.File

object ObfuscatorConfig {
    
    lateinit var input: File
    lateinit var output: File
    lateinit var libraries: List<Library>
    
    fun load(config: JsonConfig) {
        input = config.getOrThrow(ConfigSetting.INPUT) { "Missing input file in config." }
        output = config.getOrThrow(ConfigSetting.OUTPUT) { "Missing output file in config." }
        libraries = config.getOrDefault(ConfigSetting.LIBRARIES, emptyList())
    }
    
}