@file:Suppress("UNUSED_PARAMETER")

package xyz.xenondevs.vetric.transformer.obfuscation.number

import org.objectweb.asm.tree.AbstractInsnNode
import xyz.xenondevs.vetric.transformer.TransformerPriority
import xyz.xenondevs.vetric.util.asm.ASMUtils.InsnParent

open class NumberTransformer(
    val name: String,
    val priority: TransformerPriority,
    val multipleIterations: Boolean = false
) : Comparable<NumberTransformer> {
    
    var enabled = false
    var iterations = 1
    
    open fun transformInteger(insnParent: InsnParent, insn: AbstractInsnNode, value: Int) = Unit
    
    open fun transformLong(insnParent: InsnParent, insn: AbstractInsnNode, value: Long) = Unit
    
    open fun transformFloat(insnParent: InsnParent, insn: AbstractInsnNode, value: Float) = Unit
    
    open fun transformDouble(insnParent: InsnParent, insn: AbstractInsnNode, value: Double) = Unit
    
    override fun equals(other: Any?) =
        this === other || (other is NumberTransformer && other.name == this.name)
    
    override fun compareTo(other: NumberTransformer): Int {
        if (equals(other)) return 0
        
        val result = priority.compareTo(other.priority)
        return if (result == 0) 1 else result
    }
    
}