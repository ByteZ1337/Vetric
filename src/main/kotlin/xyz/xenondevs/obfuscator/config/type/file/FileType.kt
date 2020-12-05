package xyz.xenondevs.obfuscator.config.type.file

import com.google.gson.JsonElement
import xyz.xenondevs.obfuscator.config.type.SettingType
import xyz.xenondevs.obfuscator.utils.json.isString
import java.io.File

object FileType : SettingType<File>() {
    
    override fun isValid(element: JsonElement, silent: Boolean) = element.isString()
    
    override fun parseElement(element: JsonElement) = File(element.asString)
    
}