package xyz.xenondevs.vetric.transformer.obfuscation.number.medium

import org.objectweb.asm.tree.AbstractInsnNode
import xyz.xenondevs.vetric.transformer.TransformerPriority.LOWEST
import xyz.xenondevs.vetric.transformer.obfuscation.number.NumberTransformer
import xyz.xenondevs.vetric.util.asm.ASMUtils.InsnParent
import xyz.xenondevs.vetric.util.asm.insnBuilder
import xyz.xenondevs.vetric.util.asm.replace
import kotlin.random.Random
import kotlin.random.nextInt

// This transformer is extremely ineffective without string obfuscation or other number obfuscations.
object Encoder : NumberTransformer("Encoder", LOWEST, false) {
    
    override fun transformInteger(insnParent: InsnParent, insn: AbstractInsnNode, value: Int) {
        insnParent.insnList.replace(insn, insnBuilder {
            val radix = Random.nextInt(5..36)
            ldc(value.toString(radix))
            ldc(radix)
            invokestatic("java/lang/Integer", "parseInt", "(Ljava/lang/String;I)I")
        })
    }
    
    override fun transformLong(insnParent: InsnParent, insn: AbstractInsnNode, value: Long) {
        insnParent.insnList.replace(insn, insnBuilder {
            val radix = Random.nextInt(5..36)
            ldc(value.toString(radix))
            ldc(radix)
            invokestatic("java/lang/Long", "parseLong", "(Ljava/lang/String;I)J")
        })
    }
}