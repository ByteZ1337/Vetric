@file:Suppress("UNUSED_PARAMETER")

package xyz.xenondevs.vetric.transformer.obfuscation.number

import org.objectweb.asm.tree.AbstractInsnNode
import xyz.xenondevs.vetric.transformer.TransformerPriority
import xyz.xenondevs.vetric.util.asm.ASMUtils.InsnParent

open class NumberTransformer(val name: String, val priority: TransformerPriority) {
    
    open fun transformInteger(insnParent: InsnParent, insn: AbstractInsnNode, value: Int) = Unit
    
    open fun transformLong(insnParent: InsnParent, insn: AbstractInsnNode, value: Long) = Unit
    
    open fun transformFloat(insnParent: InsnParent, insn: AbstractInsnNode, value: Float) = Unit
    
    open fun transformDouble(insnParent: InsnParent, insn: AbstractInsnNode, value: Double) = Unit
    
}