package xyz.xenondevs.vetric.utils

import com.google.gson.*
import com.google.gson.reflect.TypeToken
import xyz.xenondevs.vetric.serialization.FileSerialization
import xyz.xenondevs.vetric.serialization.SupplierFactorySerialization
import xyz.xenondevs.vetric.supplier.SupplierFactory
import java.io.File
import java.lang.reflect.Type

val GSON = GsonBuilder()
    .setPrettyPrinting()
    .registerTypeHierarchyAdapter<File>(FileSerialization)
    .registerTypeHierarchyAdapter<SupplierFactory>(SupplierFactorySerialization)
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

inline fun <reified T> type(): Type = object : TypeToken<T>() {}.type

inline fun <reified T> GsonBuilder.registerTypeHierarchyAdapter(typeAdapter: Any): GsonBuilder =
    registerTypeHierarchyAdapter(T::class.java, typeAdapter)

inline fun <reified T> Gson.fromJson(jsonElement: JsonElement?): T? {
    if (jsonElement == null) return null
    return fromJson(jsonElement, type<T>())
}
