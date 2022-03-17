package xyz.xenondevs.vetric.transformer.impl.obfuscation.string

import org.objectweb.asm.tree.AbstractInsnNode
import xyz.xenondevs.bytebase.jvm.ClassWrapper
import xyz.xenondevs.vetric.config.JsonConfig
import xyz.xenondevs.vetric.utils.InstructionParent

open class StringTransformer(
    val name: String,
    val multipleIterations: Boolean = false,
) {
    
    open fun transform(clazz: ClassWrapper) {}
    
    open fun transform(parent: InstructionParent, insn: AbstractInsnNode, value: String) {}
    
    open fun loadConfig(config: JsonConfig) {}
    
    override fun hashCode() = name.hashCode()
    
    override fun equals(other: Any?) =
        this === other || other is StringTransformer && other.name == name
    
}