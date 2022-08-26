package xyz.xenondevs.vetric.serialization

import com.google.gson.*
import java.io.File
import java.lang.reflect.Type

object FileSerialization : JsonSerializer<File>, JsonDeserializer<File> {
    
    override fun serialize(file: File, type: Type, ctx: JsonSerializationContext) =
        JsonPrimitive(file.absolutePath)
    
    override fun deserialize(element: JsonElement, type: Type, ctx: JsonDeserializationContext) =
        File(element.asString)
    
}