package xyz.xenondevs.obfuscator.config.type.file

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import xyz.xenondevs.obfuscator.config.type.SettingType
import xyz.xenondevs.obfuscator.jvm.Library
import xyz.xenondevs.obfuscator.utils.json.getBoolean
import xyz.xenondevs.obfuscator.utils.json.getString
import xyz.xenondevs.obfuscator.utils.json.hasString
import java.io.File

object LibraryListType : SettingType<List<Library>>() {
    
    override fun isValid(element: JsonElement) = element.isJsonArray
    
    override fun parseElement(element: JsonElement): List<Library> {
        println("Loading libraries...")
        val libraries = ArrayList<Library>()
        element.asJsonArray!!.forEach { lib ->
            // Library path is given as JsonPrimitive
            if (lib is JsonPrimitive && lib.isString)
                libraries += getLibraries(lib.asString, false)
            // Library path is buried in a JsonObject
            else if (lib is JsonObject && lib.hasString("path")) {
                val path = lib.getString("path")!!
                val extract = lib.getBoolean("extract")
                libraries += getLibraries(path, extract)
            }
        }
        return libraries
    }
    
    @Suppress("LiftReturnOrAssignment")
    fun getLibraries(path: String, extract: Boolean): List<Library> {
        val libFile = File(path)
        require(libFile.exists()) { "The library $path was not found" }
        if (libFile.isDirectory) {
            val libraries = ArrayList<Library>()
            libFile.listFiles { file -> file.isFile && file.name.endsWith(".jar") }?.forEach {
                libraries += Library(it, extract)
            }
            return libraries
        } else return listOf(Library(libFile, extract))
    }
    
}