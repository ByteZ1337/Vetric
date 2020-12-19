package xyz.xenondevs.vetric.config.type.file

import com.google.gson.JsonElement
import xyz.xenondevs.vetric.config.type.SettingType
import xyz.xenondevs.vetric.utils.json.isString
import java.io.File

object FileType : SettingType<File>() {
    
    override fun isValid(element: JsonElement, silent: Boolean) = element.isString()
    
    override fun parseElement(element: JsonElement) = File(element.asString)
    
}