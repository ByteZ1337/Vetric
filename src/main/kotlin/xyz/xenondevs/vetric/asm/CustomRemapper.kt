package xyz.xenondevs.vetric.asm

import org.objectweb.asm.commons.SimpleRemapper

/**
 * Remapper that also checks field descriptors.
 */
class CustomRemapper(mappings: Map<String, String>) : SimpleRemapper(mappings) {
    
    override fun mapFieldName(owner: String, name: String, descriptor: String) =
        map("$owner.$name.$descriptor") ?: name
}