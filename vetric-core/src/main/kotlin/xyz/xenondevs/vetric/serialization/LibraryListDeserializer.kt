package xyz.xenondevs.vetric.serialization

import com.google.gson.*
import xyz.xenondevs.vetric.jvm.Library
import xyz.xenondevs.vetric.utils.hasBoolean
import xyz.xenondevs.vetric.utils.isString
import java.io.File
import java.lang.reflect.Type
import java.util.Collections.singletonList

/**
 * Used to deserialize the [Library] list in the config.
 */
object LibraryListDeserializer : JsonDeserializer<List<Library>> {
    
    override fun deserialize(array: JsonElement, type: Type, ctx: JsonDeserializationContext): List<Library> {
        if (array !is JsonArray)
            throw JsonParseException("Expected JsonArray, got ${array.javaClass.simpleName}")
        
        val list = array.flatMap { element ->
            if (element.isString()) {
                val file = File(element.asString)
                if (!file.exists())
                    throw JsonParseException("Library file does not exist: ${file.absolutePath}")
                
                if (file.isFile)
                    return@flatMap singletonList(Library(file, isExtracted = false))
                else
                    return@flatMap file.walkTopDown().filter { it.isFile }.map { Library(it, isExtracted = false) }.toList()
            } else if (element is JsonObject) {
                val file = File(element.get("path").asString)
                if (!file.exists())
                    throw JsonParseException("Library file does not exist: ${file.absolutePath}")
                
                val extract = element.hasBoolean("extract") && element.get("extract").asBoolean
                if (file.isFile)
                    return@flatMap singletonList(Library(file, extract))
                else
                    return@flatMap file.walkTopDown().filter { it.isFile }.map { Library(it, extract) }.toList()
            } else throw JsonParseException("Expected a JsonObject or a String for library, got ${element.javaClass.simpleName}")
        }
        
        return list
    }
}