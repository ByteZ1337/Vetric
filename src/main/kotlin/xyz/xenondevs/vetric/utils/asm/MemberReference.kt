package xyz.xenondevs.vetric.utils.asm

import org.objectweb.asm.tree.FieldInsnNode
import org.objectweb.asm.tree.MethodInsnNode

class MemberReference(val owner: String, val name: String, val desc: String) {
    
    fun process(insn: FieldInsnNode) {
        insn.owner = owner
        insn.name = name
        insn.desc = desc
    }
    
    fun process(insn: MethodInsnNode) {
        insn.owner = owner
        insn.name = name
        insn.desc = desc
    }
    
    override fun toString(): String {
        return "$owner.$name$desc"
    }
    
}