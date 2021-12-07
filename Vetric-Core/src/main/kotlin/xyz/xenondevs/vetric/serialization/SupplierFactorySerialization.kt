package xyz.xenondevs.vetric.serialization

import com.google.gson.*
import xyz.xenondevs.vetric.supplier.SupplierConfig
import xyz.xenondevs.vetric.supplier.SupplierFactory
import xyz.xenondevs.vetric.supplier.registry.SupplierRegistry
import xyz.xenondevs.vetric.supplier.registry.SupplierType
import xyz.xenondevs.vetric.supplier.registry.SupplierType.DICTIONARY
import xyz.xenondevs.vetric.supplier.registry.SupplierType.NORMAL
import xyz.xenondevs.vetric.utils.GSON
import xyz.xenondevs.vetric.utils.isString
import java.lang.reflect.Type

object SupplierFactorySerialization : JsonSerializer<SupplierFactory>, JsonDeserializer<SupplierFactory> {
    
    override fun serialize(factory: SupplierFactory, type: Type, ctx: JsonSerializationContext): JsonElement {
        val info = factory.supplierInfo
        val obj = JsonObject()
        obj.addProperty("name", info.name)
        obj.addProperty("type", info.type.name.lowercase())
        if (info.type == DICTIONARY) {
            TODO()
        }
        obj.add("settings", ctx.serialize(factory.config))
        return obj
    }
    
    override fun deserialize(element: JsonElement, type: Type, ctx: JsonDeserializationContext): SupplierFactory {
        if (element is JsonObject) {
            val name = element.get("name").asString
            val supplierType = element.get("type")?.asString?.uppercase()?.let { SupplierType.valueOf(it) } ?: NORMAL
            if (supplierType == DICTIONARY) {
                TODO()
            }
            val info = SupplierRegistry.getSupplier(name) ?: throw JsonParseException("Supplier $name not found")
            val config = ctx.deserialize<SupplierConfig>(element.get("settings"), SupplierConfig::class.java)
                ?: SupplierConfig(20, 20, true)
            return SupplierFactory(info, config)
        } else if (element.isString()) {
            val name = element.asString
            val info = SupplierRegistry.getSupplier(name) ?: throw JsonParseException("Supplier $name not found")
            return SupplierFactory(info, SupplierConfig(20, 20, false))
        }
        throw JsonParseException("Invalid supplier factory: " + GSON.toJson(element))
    }
    
}