package xyz.xenondevs.vetric.transformer.obfuscation.string

import org.objectweb.asm.tree.FieldNode
import org.objectweb.asm.tree.LdcInsnNode
import org.objectweb.asm.tree.MethodNode
import xyz.xenondevs.vetric.config.type.TransformerConfig
import xyz.xenondevs.vetric.jvm.ClassWrapper
import xyz.xenondevs.vetric.transformer.ClassTransformer
import xyz.xenondevs.vetric.transformer.TransformerPriority
import xyz.xenondevs.vetric.util.filterTypeAnd

abstract class StringTransformer(
    name: String,
    config: TransformerConfig,
    val stringFilter: (String) -> Boolean = { true },
    priority: TransformerPriority = TransformerPriority.NORMAL
) : ClassTransformer(name, config, priority) {
    
    override fun transformMethod(method: MethodNode) {
        method.instructions.filterTypeAnd<LdcInsnNode> { it.cst is String && stringFilter(it.cst as String) }.forEach {
            transformString(method, it, it.cst as String)
        }
    }
    
    override fun transformClass(clazz: ClassWrapper) = Unit
    
    override fun transformField(field: FieldNode) = Unit
    
    abstract fun transformString(method: MethodNode, instruction: LdcInsnNode, string: String)
}