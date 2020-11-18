package xyz.xenondevs.obfuscator.config.type.file

import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import xyz.xenondevs.obfuscator.config.type.SettingType
import java.io.File

object FileType : SettingType<File>() {
    
    override fun isValid(element: JsonElement) = element is JsonPrimitive && element.isString
    
    override fun parseElement(element: JsonElement) = File(element.asString)
    
}