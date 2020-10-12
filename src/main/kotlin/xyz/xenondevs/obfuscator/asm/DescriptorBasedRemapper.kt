package xyz.xenondevs.obfuscator.asm

import org.objectweb.asm.commons.SimpleRemapper

/**
 * Remapper that also looks at field descriptors.
 */
class DescriptorBasedRemapper(mappings: Map<String, String>) : SimpleRemapper(mappings) {

    override fun mapFieldName(owner: String, name: String, descriptor: String) =
        map("$owner.$name.$descriptor") ?: name
}