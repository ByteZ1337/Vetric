package xyz.xenondevs.obfuscator.config.type.transformer

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import xyz.xenondevs.obfuscator.config.type.SupplierType
import xyz.xenondevs.obfuscator.transformers.TransformerRegistry
import xyz.xenondevs.obfuscator.transformers.renamer.Renamer
import xyz.xenondevs.obfuscator.utils.json.getBoolean

object RenamerConfig : TransformerType(Renamer) {
    
    override fun parse(obj: JsonObject) {
        super.parse(obj)
        if (Renamer in TransformerRegistry.transformers) {
            if (obj.has("supplier")) {
                val supplierElement = obj["supplier"]
                when {
                    SupplierType.isValid(supplierElement, true) -> handleSingleSupplier(supplierElement)
                    supplierElement is JsonObject -> handleMultipleSuppliers(supplierElement)
                    else -> error("Invalid element for Renamer supplier.")
                }
            }
            Renamer.repeatNames = obj.getBoolean("repeatNames")
            Renamer.removePackages = obj.getBoolean("removePackages")
            Renamer.renamePackages = obj.getBoolean("packages")
            Renamer.renameClasses = obj.getBoolean("classes", true)
            Renamer.renameFields = obj.getBoolean("fields", true)
            Renamer.renameMethods = obj.getBoolean("methods", true)
            
            if (Renamer.renamePackages && Renamer.removePackages) {
                println("RenamePackages and RemovePackages is set to true. Defaulting to removing packages")
                Renamer.removePackages = false
            }
        }
    }
    
    private fun handleSingleSupplier(element: JsonElement) {
        val supplier = SupplierType.parse(element)!!
        Renamer.packagesSupplier = supplier
        Renamer.classesSupplier = supplier
        Renamer.fieldsSupplier = supplier
        Renamer.methodsSupplier = supplier
    }
    
    private fun handleMultipleSuppliers(obj: JsonObject) {
        if (obj.has("packages") && SupplierType.isValid(obj["packages"]))
            Renamer.packagesSupplier = SupplierType.parseElement(obj["packages"])
        if (obj.has("classes") && SupplierType.isValid(obj["classes"]))
            Renamer.classesSupplier = SupplierType.parseElement(obj["classes"])
        if (obj.has("fields") && SupplierType.isValid(obj["fields"]))
            Renamer.fieldsSupplier = SupplierType.parseElement(obj["fields"])
        if (obj.has("methods") && SupplierType.isValid(obj["methods"]))
            Renamer.methodsSupplier = SupplierType.parseElement(obj["methods"])
    }
    
}