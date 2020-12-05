package xyz.xenondevs.obfuscator.config.type

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import xyz.xenondevs.obfuscator.config.type.file.FileType
import xyz.xenondevs.obfuscator.suppliers.DictionarySupplier
import xyz.xenondevs.obfuscator.suppliers.StringSupplier
import xyz.xenondevs.obfuscator.suppliers.Supplier
import xyz.xenondevs.obfuscator.utils.json.getString
import xyz.xenondevs.obfuscator.utils.json.hasString

// TODO cleanup
@Suppress("LiftReturnOrAssignment")
object SupplierType : SettingType<StringSupplier>() {
    
    // TODO list default suppliers and dictionaries
    override fun isValid(element: JsonElement, silent: Boolean): Boolean {
        if (element is JsonPrimitive) {
            if (element.isString && Supplier[element.asString] != null)
                return true
            
            require(silent) { "Supplier ${element.asString} could not be found." }
        } else if (element is JsonObject) {
            if (element.hasString("name")) {
                val name = element.getString("name")!!
                
                if (name.equals("dict", true) || name.equals("dictionary", true))
                    return isValidDictSupplier(element, silent)
                else if (Supplier[name] != null)
                    return true
                else require(silent) { "$name is not a valid supplier." }
            } else require(silent) { "Missing name property for supplier in config." }
        }
        
        require(silent) { "Invalid element for supplier." }
        return false
    }
    
    private fun isValidDictSupplier(obj: JsonObject, silent: Boolean): Boolean {
        if (obj.hasString("dict")) {
            val dict = obj.getString("dict")!!
            if (dict.equals("custom", true)) {
                if (obj.hasString("path"))
                    return true
                require(silent) { "Please provide a path for the custom dictionary." }
            } else {
                if (DictionarySupplier.DEFAULT.any { it.name.equals(dict, true) })
                    return true
                require(silent) { "Unable to find the dictionary $dict." }
            }
        } else require(silent) { "Missing name of dictionary." }
        return false
    }
    
    override fun parseElement(element: JsonElement): StringSupplier {
        if (element is JsonPrimitive)
            return Supplier[element.asString]!!.newInstance(10, 20)
        element as JsonObject
        val name = element.getString("name")!!
        if (name.equals("dict", true) || name.equals("dictionary", true))
            return parseDictionary(element)
        else return parseCharSupplier(element)
    }
    
    private fun parseCharSupplier(obj: JsonObject): StringSupplier {
        val supplier = Supplier[obj.getString("name")!!]!!
        if (obj.has("length")) {
            val (min, max) = RangeType.parse(obj["length"]!!)!!
            return supplier.newInstance(min, max)
        }
        return supplier.newInstance(10, 20)
    }
    
    private fun parseDictionary(obj: JsonObject): DictionarySupplier {
        val dict = obj.getString("dict")!!
        if (dict.equals("custom", true)) {
            val file = FileType.parse(obj["path"])!!
            require(file.exists()) { "The given path ${file.absolutePath} does not exist." }
            require(file.isFile) { "The given path is a directory." }
            
            val lines = file.readLines()
            require(lines.isNotEmpty()) { "The given dictionary is empty." }
            
            return DictionarySupplier(file.name, lines)
        } else return DictionarySupplier.DEFAULT.first { it.name.equals(dict, true) }
    }
    
}