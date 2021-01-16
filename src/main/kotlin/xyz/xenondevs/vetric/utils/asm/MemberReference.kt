package xyz.xenondevs.vetric.utils.asm

import org.objectweb.asm.tree.FieldInsnNode
import org.objectweb.asm.tree.MethodInsnNode

class MemberReference(val owner: String, val name: String, val desc: String) {
    
    fun transform(insn: FieldInsnNode) {
        insn.owner = owner
        insn.name = name
        insn.desc = desc
    }
    
    fun transform(insn: MethodInsnNode) {
        insn.owner = owner
        insn.name = name
        insn.desc = desc
    }
    
    override fun toString() =
        if (desc.contains('(')) "$owner.$name$desc" else "$owner.$name.$desc"
    
}