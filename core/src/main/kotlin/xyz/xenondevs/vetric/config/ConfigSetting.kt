package xyz.xenondevs.vetric.config

import xyz.xenondevs.vetric.config.type.SettingType
import xyz.xenondevs.vetric.config.type.file.FileType
import xyz.xenondevs.vetric.config.type.file.LibraryListType

enum class ConfigSetting(val path: String, val type: SettingType<*>) {
    
    INPUT("files.input", FileType),
    OUTPUT("files.output", FileType),
    LIBRARIES("files.libraries", LibraryListType);
    
}