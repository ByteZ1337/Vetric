package xyz.xenondevs.vetric.transformer.impl.obfuscation.misc.kotlin

import org.objectweb.asm.tree.InsnList
import org.objectweb.asm.tree.LdcInsnNode
import org.objectweb.asm.tree.MethodInsnNode
import org.objectweb.asm.tree.MethodNode
import xyz.xenondevs.vetric.transformer.ClassTransformer
import xyz.xenondevs.vetric.transformer.TransformerPriority
import xyz.xenondevs.vetric.utils.filterTypeAnd

// TODO: add an option to use mappings instead of removing string
object KotlinIntrinsicsReplacer : ClassTransformer("KotlinIntrinsicsReplacer", TransformerPriority.NORMAL) {
    
    override fun transformMethod(method: MethodNode) {
        val instructions = method.instructions
        instructions.asSequence()
            .filterTypeAnd<MethodInsnNode> { it.owner == "kotlin/jvm/internal/Intrinsics" }
            .forEach { insn ->
                when (insn.name) {
                    "checkNotNull"
                    -> processBasic(instructions, insn)
                    
                    "checkExpressionValueIsNotNull",
                    "checkNotNullExpressionValue",
                    "checkParameterIsNotNull",
                    "checkNotNullParameter"
                    -> processOneParameter(insn)
                    
                    "checkReturnedValueIsNotNull",
                    "checkFieldIsNotNull"
                    -> processComplex(insn)
                }
            }
    }
    
    private fun processBasic(instructions: InsnList, insn: MethodInsnNode) {
        if (insn.previous is LdcInsnNode) {
            instructions.remove(insn.previous)
            insn.desc = "(Ljava/lang/Object;)V"
        }
    }
    
    private fun processOneParameter(insn: MethodInsnNode) {
        val prev = insn.previous
        if (prev is LdcInsnNode)
            prev.cst = ""
    }
    
    private fun processComplex(insn: MethodInsnNode) {
        if (insn.desc == "(Ljava/lang/Object;Ljava/lang/String;)V") {
            processOneParameter(insn)
            return
        }
        val classNameLdc = insn.previous
        val memberNameLdc = classNameLdc.previous
        if (classNameLdc is LdcInsnNode && memberNameLdc is LdcInsnNode) {
            classNameLdc.cst = ""
            memberNameLdc.cst = ""
        }
    }
}