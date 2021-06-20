package xyz.xenondevs.vetric.util.json

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive

fun JsonElement.isString() = this is JsonPrimitive && this.isString

fun JsonElement.isNumber() = this is JsonPrimitive && this.isNumber

fun JsonElement.isBoolean() = this is JsonPrimitive && this.isBoolean

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

fun JsonObject.getString(property: String, default: String? = null) =
    if (hasString(property)) get(property).asString else default

fun JsonObject.getNumber(property: String, default: Number? = null) =
    if (hasNumber(property)) get(property).asNumber else default

fun JsonObject.getInt(property: String, default: Int? = null) =
    if (hasNumber(property)) get(property).asInt else default

fun JsonObject.getDouble(property: String, default: Double? = null) =
    if (hasNumber(property)) get(property).asDouble else default

fun JsonObject.getBoolean(property: String, default: Boolean = false) =
    if (hasBoolean(property)) get(property).asBoolean else default

fun <T> JsonArray.toStringList(consumer: (List<String>) -> T) =
    consumer(this.filter(JsonElement::isString).map(JsonElement::getAsString))

operator fun JsonObject.set(property: String, value: JsonElement) = add(property, value)
