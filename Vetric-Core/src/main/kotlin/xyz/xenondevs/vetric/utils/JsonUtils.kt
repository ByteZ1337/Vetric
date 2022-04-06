package xyz.xenondevs.vetric.utils

import com.google.gson.*
import com.google.gson.reflect.TypeToken
import xyz.xenondevs.vetric.jvm.Library
import xyz.xenondevs.vetric.serialization.FileSerialization
import xyz.xenondevs.vetric.serialization.LibraryListDeserializer
import xyz.xenondevs.vetric.serialization.SupplierFactorySerialization
import xyz.xenondevs.vetric.supplier.SupplierFactory
import java.io.File
import java.lang.reflect.Type

val GSON = GsonBuilder()
    .setPrettyPrinting()
    .registerTypeHierarchyAdapter<File>(FileSerialization)
    .registerTypeHierarchyAdapter<SupplierFactory>(SupplierFactorySerialization)
    .registerTypeHierarchyAdapter<List<Library>>(LibraryListDeserializer)
    .create()!!

operator fun JsonObject.set(property: String, value: JsonElement) = add(property, value)

fun JsonElement.isString() =
    this is JsonPrimitive && isString

fun JsonElement.isBoolean() =
    this is JsonPrimitive && isBoolean

fun JsonElement.isNumber() =
    this is JsonPrimitive && isNumber

fun JsonObject.hasString(property: String) =
    has(property) && this[property].isString()

fun JsonObject.hasNumber(property: String) =
    has(property) && this[property].isNumber()

fun JsonObject.hasBoolean(property: String) =
    has(property) && this[property].isBoolean()

fun JsonObject.hasObject(property: String) =
    has(property) && this[property] is JsonObject

fun JsonObject.hasArray(property: String) =
    has(property) && this[property] is JsonArray

fun JsonObject.getString(property: String) = if (hasString(property)) get(property).asString else null

fun JsonObject.getNumber(property: String) = if (hasNumber(property)) get(property).asNumber else null

fun JsonObject.getInt(property: String) = if (hasNumber(property)) get(property).asInt else null

fun JsonObject.getLong(property: String) = if (hasNumber(property)) get(property).asLong else null

fun JsonObject.getDouble(property: String) = if (hasNumber(property)) get(property).asDouble else null

fun JsonObject.getFloat(property: String) = if (hasNumber(property)) get(property).asFloat else null

fun JsonObject.getString(property: String, default: String): String = if (hasString(property)) get(property).asString else default

fun JsonObject.getNumber(property: String, default: Number): Number = if (hasNumber(property)) get(property).asNumber else default

fun JsonObject.getInt(property: String, default: Int) = if (hasNumber(property)) get(property).asInt else default

fun JsonObject.getDouble(property: String, default: Double) = if (hasNumber(property)) get(property).asDouble else default

fun JsonObject.getFloat(property: String, default: Float) = if (hasNumber(property)) get(property).asFloat else default

fun JsonObject.getBoolean(property: String, default: Boolean = false) = if (hasBoolean(property)) get(property).asBoolean else default

inline fun <reified T> type(): Type = object : TypeToken<T>() {}.type

inline fun <reified T> GsonBuilder.registerTypeHierarchyAdapter(typeAdapter: Any): GsonBuilder =
    registerTypeHierarchyAdapter(T::class.java, typeAdapter)

inline fun <reified T> Gson.fromJson(jsonElement: JsonElement?): T? {
    if (jsonElement == null) return null
    return fromJson(jsonElement, type<T>())
}
