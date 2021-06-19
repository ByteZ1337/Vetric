package xyz.xenondevs.vetric.exclusion

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import xyz.xenondevs.vetric.config.type.SettingType
import xyz.xenondevs.vetric.util.json.getString
import xyz.xenondevs.vetric.util.json.hasString
import xyz.xenondevs.vetric.util.json.isString

object ExclusionListType : SettingType<List<String>>() {
    
    override fun isValid(element: JsonElement, silent: Boolean) = element is JsonArray
    
    override fun parseElement(element: JsonElement): List<String> {
        val exlusions = ArrayList<String>()
        element as JsonArray
        element.forEachIndexed { index, arrElement ->
            when {
                arrElement is JsonObject -> handleMemberReference(index, arrElement, exlusions)
                arrElement.isString() -> exlusions.add(arrElement.asString)
                else -> println("Invalid number transformer at index $index. Skipping...")
            }
        }
        return exlusions
    }
    
    private fun handleMemberReference(index: Int, obj: JsonObject, list: MutableList<String>) {
        if (!obj.hasString("name")) {
            println("No name provided for exclusion at index $index. Skipping...")
            return
        }
        val builder = StringBuilder(obj.getString("name"))
        if (obj.hasString("owner")) {
            builder.append(".${obj.getString("owner")}")
            if (obj.hasString("descriptor"))
                builder.append(".${obj.getString("descriptor")}")
        }
        list.add(builder.toString())
    }
}