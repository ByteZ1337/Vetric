package xyz.xenondevs.vetric.transformer.impl.obfuscation.string

import org.objectweb.asm.tree.AbstractInsnNode
import org.objectweb.asm.tree.LdcInsnNode
import xyz.xenondevs.bytebase.jvm.ClassWrapper
import xyz.xenondevs.bytebase.jvm.JavaArchive
import xyz.xenondevs.vetric.config.JsonConfig
import xyz.xenondevs.vetric.transformer.Transformer
import xyz.xenondevs.vetric.transformer.TransformerPriority
import xyz.xenondevs.vetric.utils.InstructionParent
import xyz.xenondevs.vetric.utils.filterTypeSub

open class StringTransformer(
    name: String,
    priority: TransformerPriority,
    val multipleIterations: Boolean = false,
) : Transformer(name, priority) {
    
    open fun transform(clazz: ClassWrapper) {}
    
    open fun transform(parent: InstructionParent, insn: AbstractInsnNode, value: String) {}
    
    open fun loadConfig(config: JsonConfig) {}
    
    override fun transform(jar: JavaArchive) {
        // TODO move to parent for optimization
        jar.classes.forEach { clazz ->
            transform(clazz)
            clazz.methods.asSequence().forEach { method ->
                method.instructions
                    .asSequence()
                    .filterTypeSub<LdcInsnNode, String> { it.cst }
                    .forEach { insn ->
                        transform(InstructionParent(jar, clazz, method, method.instructions), insn, insn.cst as String)
                    }
            }
        }
    }
    
    override fun hashCode() = name.hashCode()
    
    override fun equals(other: Any?) =
        this === other || other is StringTransformer && other.name == name
    
}