@file:Suppress("MemberVisibilityCanBePrivate")

package xyz.xenondevs.vetric.config

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import org.intellij.lang.annotations.RegExp
import xyz.xenondevs.vetric.utils.*
import java.io.File

@RegExp
private val PATH_SPLIT_REGEX = Regex("""(?<!\\)\Q.\E""")

/**
 * A class that represents a JSON config file. Also implements XPath-like queries.
 */
open class JsonConfig(val supplier: ConfigSupplier, autoInit: Boolean) {
    
    init {
        if (autoInit) reload()
    }
    
    lateinit var config: JsonObject
    
    constructor(file: File, autoInit: Boolean) : this(FileConfigSupplier(file), autoInit)
    
    constructor(config: JsonObject) : this(DirectConfigSupplier(config), true)
    
    fun reload() {
        config = supplier.get()
    }
    
    fun save() {
        supplier.set(GSON.toJson(config))
    }
    
    /**
     * Prepares a path of JsonObjects to be used in the [get] and [set] methods.
     */
    fun generatePath(path: List<String>): JsonObject {
        val pre = get(path)
        if (pre is JsonObject)
            return pre.asJsonObject
        
        var current = this.config
        path.forEach {
            val pathPart = it.replace("\\.", ".")
            if (current[pathPart] !is JsonObject)
                current[pathPart] = JsonObject()
            current = current[pathPart].asJsonObject
        }
        return current
    }
    
    //<editor-fold desc="Setting Values" defaultstate="collapsed">
    /**
     * Sets a value in the config.
     */
    operator fun set(path: String, element: JsonElement) {
        val pathParts = path.split(PATH_SPLIT_REGEX)
        var parentObject = this.config
        if (pathParts.size > 1)
            parentObject = generatePath(pathParts.dropLast(1))
        parentObject[pathParts.last().replace("\\.", ".")] = element
    }
    
    operator fun set(path: String, value: String) = set(path, JsonPrimitive(value))
    
    operator fun set(path: String, value: Number) = set(path, JsonPrimitive(value))
    
    operator fun set(path: String, value: Boolean) = set(path, JsonPrimitive(value))
    
    operator fun set(path: String, value: Char) = set(path, JsonPrimitive(value))
    
    operator fun set(path: String, value: List<String>) {
        val array = JsonArray()
        value.stream().map(::JsonPrimitive).forEach(array::add)
        set(path, array)
    }
    
    inline operator fun <reified T> set(path: String, obj: T) {
        if (obj is JsonElement)
            set(path, obj)
        set(path, GSON.toJsonTree(obj))
    }
    
    fun addToArray(path: String, elements: List<JsonElement>) {
        var array = getArray(path)
        if (array == null) {
            array = JsonArray()
            set(path, array)
        }
        elements.forEach(array::add)
    }
    
    fun addToArray(path: String, vararg elements: JsonElement) =
        addToArray(path, elements.toList())
    
    fun addToArray(path: String, vararg values: String) =
        addToArray(path, values.map(::JsonPrimitive))
    
    fun addToArray(path: String, vararg values: Number) =
        addToArray(path, values.map(::JsonPrimitive))
    //</editor-fold>
    
    //<editor-fold desc="Removing values" defaultstate="collapsed">
    /**
     * Removes the value at the given path.
     */
    fun remove(path: String) {
        val pathParts = path.split(PATH_SPLIT_REGEX)
        if (pathParts.size == 1) {
            this.config.remove(path)
            return
        }
        
        val element = get(pathParts.dropLast(1))
        if (element !is JsonObject)
            return
        val parent = element.asJsonObject
        parent.remove(pathParts.last().replace("\\.", "."))
        if (parent.size() == 0)
            remove(pathParts.dropLast(1).joinToString("."))
    }
    
    fun removeFromArray(path: String, filter: (JsonElement) -> Boolean) {
        val array = getArray(path) ?: return
        val newArray = JsonArray()
        array.filterNot(filter).forEach(newArray::add)
        
        if (newArray.size() == 0) remove(path)
        else set(path, newArray)
    }
    
    fun removeFromArray(path: String, vararg values: String) = removeFromArray(path) { element ->
        element.isString() && values.any { it == element.asString }
    }
    
    fun removeFromArray(path: String, vararg values: Number) = removeFromArray(path) { element ->
        element.isNumber() && values.any { it.toString() == element.asNumber.toString() }
    }
    //</editor-fold>
    
    //<editor-fold desc="Getting Values" defaultstate="collapsed">
    /**
     * Gets the JsonElement at the specified path or null if it doesn't exist.
     */
    open fun get(path: List<String>): JsonElement? {
        var current = this.config
        path.dropLast(1).forEach { pathPart ->
            val property = pathPart.replace("\\.", ".")
            if (current[property] !is JsonObject)
                return null
            current = current[property].asJsonObject
        }
        
        return current[path.last().replace("\\.", ".")]
    }
    
    fun getElement(path: String) = get(PATH_SPLIT_REGEX.split(path))
    
    @JvmName("getReified")
    inline operator fun <reified T> get(path: String): T? {
        val element = getElement(path) ?: return null
        if (T::class.java.isAssignableFrom(element.javaClass))
            return element as T
        return GSON.fromJson<T>(element)
    }
    
    fun getObject(path: String) = get<JsonObject>(path)
    
    fun getPrimitive(path: String) = get<JsonPrimitive>(path)
    
    fun getArray(path: String) = get<JsonArray>(path)
    
    fun getString(path: String) = getPrimitive(path)?.asString
    
    fun getNumber(path: String) = getPrimitive(path)?.asNumber
    
    fun getInt(path: String) = getNumber(path)?.toInt()
    
    fun getLong(path: String) = getNumber(path)?.toLong()
    
    fun getDouble(path: String) = getNumber(path)?.toDouble()
    
    fun getFloat(path: String) = getNumber(path)?.toFloat()
    
    fun getBoolean(path: String) = getPrimitive(path)?.asBoolean ?: false
    
    fun getChar(path: String) = getPrimitive(path)?.asCharacter
    
    fun getStringList(path: String): ArrayList<String>? {
        val array = getArray(path) ?: return null
        return ArrayList(array.map(JsonElement::getAsString))
    }
    
    fun getOrDefault(path: String, default: String) = getString(path) ?: default
    
    fun getOrThrow(path: String, lazyMessage: () -> String = { "Missing $path in config" }): String {
        require(path in this, lazyMessage)
        return getString(path)!!
    }
    //</editor-fold>
    
    operator fun contains(path: String) = getElement(path) != null
    
    @JvmName("containsReified")
    inline operator fun <reified T> contains(path: String) where T : JsonElement = getElement(path) is T
    
    fun containsString(path: String) = getPrimitive(path).let { it != null && it.isString }
    
    fun containsNumber(path: String) = getPrimitive(path).let { it != null && it.isNumber }
    
    fun containsBoolean(path: String) = getPrimitive(path).let { it != null && it.isBoolean }
    
    operator fun minusAssign(path: String) = remove(path)
    
}