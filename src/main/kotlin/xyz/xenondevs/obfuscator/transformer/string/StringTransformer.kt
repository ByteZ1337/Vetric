package xyz.xenondevs.obfuscator.transformer.string

import org.objectweb.asm.tree.FieldNode
import org.objectweb.asm.tree.LdcInsnNode
import org.objectweb.asm.tree.MethodNode
import xyz.xenondevs.obfuscator.jvm.ClassWrapper
import xyz.xenondevs.obfuscator.transformer.ClassTransformer
import xyz.xenondevs.obfuscator.util.filterTypeAnd

abstract class StringTransformer(name: String, val stringFilter: (String) -> Boolean = { true }) :
    ClassTransformer(name) {

    override fun transformMethod(method: MethodNode) {
        method.instructions.filterTypeAnd<LdcInsnNode> { it.cst is String && stringFilter(it.cst as String) }.forEach {
            transformString(method, it, it.cst as String)
        }
    }

    override fun transformClass(clazz: ClassWrapper) = Unit

    override fun transformField(field: FieldNode) = Unit

    abstract fun transformString(method: MethodNode, instruction: LdcInsnNode, string: String)
}