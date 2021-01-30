package xyz.xenondevs.vetric.transformer.obfuscation.number

import org.objectweb.asm.Opcodes.*
import org.objectweb.asm.tree.AbstractInsnNode
import org.objectweb.asm.tree.FieldNode
import org.objectweb.asm.tree.LdcInsnNode
import org.objectweb.asm.tree.MethodNode
import xyz.xenondevs.vetric.config.type.TransformerConfig
import xyz.xenondevs.vetric.jvm.ClassWrapper
import xyz.xenondevs.vetric.transformer.ClassTransformer
import xyz.xenondevs.vetric.transformer.TransformerPriority
import xyz.xenondevs.vetric.transformer.obfuscation.number.light.XorTransformer
import xyz.xenondevs.vetric.util.asm.ASMUtils
import xyz.xenondevs.vetric.util.asm.ASMUtils.InsnParent

object NumberObfuscator : ClassTransformer("NumberObfuscator", TransformerConfig(NumberObfuscator::class), TransformerPriority.LOW) {
    
    private val transformers = sortedSetOf(compareBy(NumberTransformer::priority),
        XorTransformer
    )
    
    override fun transformMethod(method: MethodNode) {
        transformers.forEach { transformer ->
            method.instructions.forEach insnLoop@{ insn ->
                val number = when {
                    insn is LdcInsnNode && insn.cst is Number -> insn.cst as Number
                    insn.opcode in ICONST_0..ICONST_5 || insn.opcode == BIPUSH || insn.opcode == SIPUSH -> ASMUtils.getInt(insn)
                    insn.opcode in LCONST_0..LCONST_1 -> ASMUtils.getLong(insn)
                    insn.opcode in FCONST_0..FCONST_2 -> ASMUtils.getFloat(insn)
                    insn.opcode in DCONST_0..DCONST_1 -> ASMUtils.getDouble(insn)
                    else -> return@insnLoop
                }
                callTransformer(transformer, method, insn, number)
            }
        }
    }
    
    fun callTransformer(transformer: NumberTransformer, method: MethodNode, insn: AbstractInsnNode, value: Number) {
        val parent = InsnParent(currentJar, currentClass, method, method.instructions)
        
        when (value) {
            is Int -> transformer.transformInteger(parent, insn, value)
            is Long -> transformer.transformLong(parent, insn, value)
            is Float -> transformer.transformFloat(parent, insn, value)
            is Double -> transformer.transformDouble(parent, insn, value)
        }
    }
    
    override fun transformClass(clazz: ClassWrapper) = Unit
    
    override fun transformField(field: FieldNode) = Unit
}