package xyz.xenondevs.vetric.config

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import java.io.File

/**
 * Fully configuring Vetric via the CLI will be coming in the future. So for now, this is needed to
 * wrap File access. (e.g. load a default config from the jar)
 */
open class ConfigSupplier(val reader: () -> JsonObject, val writer: (String) -> Unit) {
    fun get(): JsonObject = reader()
    fun set(value: String) = writer(value)
}

/**
 * Simple wrapper for a file.
 *
 * Note: Checking if the file exists is redundant since the launcher already checks.
 */
class FileConfigSupplier(private val file: File) : ConfigSupplier(
    {
        val text = file.readText()
        if (text.isEmpty()) JsonObject()
        else JsonParser.parseString(text).asJsonObject
    }, { file.writeText(it) })

/**
 * Directly wraps a JsonObject. Used for transformer configs.
 */
class DirectConfigSupplier(private val json: JsonObject) : ConfigSupplier({ json }, { })