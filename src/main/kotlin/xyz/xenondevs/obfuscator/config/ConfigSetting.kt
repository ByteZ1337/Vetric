package xyz.xenondevs.obfuscator.config

import xyz.xenondevs.obfuscator.config.type.SettingType
import xyz.xenondevs.obfuscator.config.type.file.FileType
import xyz.xenondevs.obfuscator.config.type.file.LibraryListType

enum class ConfigSetting(val path: String, val type: SettingType<*>) {
    
    INPUT("files.input", FileType),
    OUTPUT("files.output", FileType),
    LIBRARIES("files.libraries", LibraryListType);
    
}