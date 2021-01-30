package xyz.xenondevs.vetric.transformer.obfuscation.number.light

import org.objectweb.asm.tree.AbstractInsnNode
import xyz.xenondevs.vetric.transformer.TransformerPriority
import xyz.xenondevs.vetric.transformer.obfuscation.number.NumberTransformer
import xyz.xenondevs.vetric.util.asm.ASMUtils.InsnParent
import xyz.xenondevs.vetric.util.asm.insnBuilder
import xyz.xenondevs.vetric.util.replace
import kotlin.random.Random

object XorTransformer : NumberTransformer("Xor", TransformerPriority.NORMAL) {
    
    override fun transformInteger(insnParent: InsnParent, insn: AbstractInsnNode, value: Int) {
        val key = Random.nextInt()
        insnParent.insnList.replace(insn, insnBuilder {
            ldc(value xor key)
            ldc(key)
            ixor()
        })
    }
    
    override fun transformLong(insnParent: InsnParent, insn: AbstractInsnNode, value: Long) {
        val key = Random.nextLong()
        insnParent.insnList.replace(insn, insnBuilder {
            ldc(value xor key)
            ldc(key)
            lxor()
        })
    }
}