package xyz.xenondevs.obfuscator.utils.json

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive

operator fun JsonObject.set(property: String, value: JsonElement) = add(property, value)

fun JsonObject.hasString(property: String) =
    has(property) && this[property] is JsonPrimitive && (this[property] as JsonPrimitive).isString

fun JsonObject.hasNumber(property: String) =
    has(property) && this[property] is JsonPrimitive && (this[property] as JsonPrimitive).isNumber

fun JsonObject.hasBoolean(property: String) =
    has(property) && this[property] is JsonPrimitive && (this[property] as JsonPrimitive).isBoolean

fun JsonObject.hasObject(property: String) =
    has(property) && this[property] is JsonObject

fun JsonObject.getString(property: String, default: String? = null) = if (hasString(property)) get(property).asString else default

fun JsonObject.getNumber(property: String, default: Number? = null) = if (hasNumber(property)) get(property).asNumber else default

fun JsonObject.getInt(property: String, default: Int? = null) = if (hasNumber(property)) get(property).asInt else default

fun JsonObject.getDouble(property: String, default: Double? = null) = if (hasNumber(property)) get(property).asDouble else default

fun JsonObject.getBoolean(property: String, default: Boolean = false) = if (hasBoolean(property)) get(property).asBoolean else default