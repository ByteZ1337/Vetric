package xyz.xenondevs.vetric.utils

import com.google.gson.*
import com.google.gson.reflect.TypeToken
import xyz.xenondevs.vetric.serialization.FileSerialization
import java.io.File
import java.lang.reflect.Type

val GSON = GsonBuilder()
    .setPrettyPrinting()
    .registerTypeHierarchyAdapter<File>(FileSerialization)
    .create()!!

operator fun JsonObject.set(property: String, value: JsonElement) = add(property, value)

fun JsonElement.isString() =
    this is JsonPrimitive && isString

fun JsonElement.isBoolean() =
    this is JsonPrimitive && isBoolean

fun JsonElement.isNumber() =
    this is JsonPrimitive && isNumber

inline fun <reified T> type(): Type = object : TypeToken<T>() {}.type

inline fun <reified T> GsonBuilder.registerTypeHierarchyAdapter(typeAdapter: Any): GsonBuilder =
    registerTypeHierarchyAdapter(T::class.java, typeAdapter)

inline fun <reified T> Gson.fromJson(jsonElement: JsonElement?): T? {
    if (jsonElement == null) return null
    return fromJson(jsonElement, type<T>())
}
