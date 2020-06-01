package xyz.xenondevs.obfuscator.util

import org.objectweb.asm.Opcodes.*
import org.objectweb.asm.Type
import org.objectweb.asm.tree.AbstractInsnNode
import org.objectweb.asm.tree.InsnNode
import org.objectweb.asm.tree.IntInsnNode
import org.objectweb.asm.tree.LdcInsnNode


object AsmUtils {

    /* ------------------ Access Checks ------------------ */

    fun isPublic(access: Int): Boolean = access and ACC_PUBLIC != 0

    fun isPrivate(access: Int): Boolean = access and ACC_PRIVATE != 0

    fun isProtected(access: Int): Boolean = access and ACC_PROTECTED != 0

    fun isStatic(access: Int): Boolean = access and ACC_STATIC != 0

    fun isFinal(access: Int): Boolean = access and ACC_FINAL != 0

    fun isAbstract(access: Int): Boolean = access and ACC_ABSTRACT != 0

    fun isEnum(access: Int): Boolean = access and ACC_ENUM != 0

    fun isInterface(access: Int): Boolean = access and ACC_INTERFACE != 0

    /* ------------------ General Utilities ------------------ */

    fun isReturn(opcode: Int): Boolean = opcode == IRETURN || opcode == RETURN

    fun buildMethodDesc(returnType: Type, params: List<Type>): String {
        var desc = "("
        params.forEach { desc += it.descriptor }
        desc += ")${returnType.descriptor}"
        return desc
    }

    /* ------------------ Int Utilities ------------------ */

    fun isIntInsn(insn: AbstractInsnNode): Boolean {
        return when {
            insn.opcode in ICONST_M1..ICONST_5 -> true
            insn is IntInsnNode -> true
            insn is LdcInsnNode && insn.cst is Int -> true
            else -> false
        }
    }

    fun getInt(insn: AbstractInsnNode): Int {
        return when {
            insn.opcode in ICONST_M1..ICONST_5 -> insn.opcode - 3
            insn is IntInsnNode -> insn.operand
            insn is LdcInsnNode && insn.cst is Int -> insn.cst as Int
            else -> throw IllegalArgumentException("Instruction can't return int")
        }
    }

    fun getInsn(int: Int): AbstractInsnNode {
        return when (int) {
            in 0..5 -> InsnNode(int + 3)
            in -128..127 -> IntInsnNode(BIPUSH, int)
            in -32768..32767 -> IntInsnNode(SIPUSH, int)
            else -> LdcInsnNode(int)
        }
    }

    /* ------------------ Long Utilities ------------------ */

    fun getLong(insn: AbstractInsnNode): Long {
        return when {
            insn.opcode in LCONST_0..LCONST_1 -> (insn.opcode - 9).toLong()
            insn is LdcInsnNode && insn.cst is Long -> insn.cst as Long
            else -> throw IllegalArgumentException("Instruction can't return long")
        }
    }

    fun getInsn(long: Long): AbstractInsnNode {
        return when (long) {
            in 0L..1L -> InsnNode(long.toInt() + 9)
            else -> LdcInsnNode(long)
        }
    }

    /* ------------------ Float Utilities ------------------ */

    fun getFloat(insn: AbstractInsnNode): Float {
        return when {
            insn.opcode in FCONST_0..FCONST_2 -> (insn.opcode - 11).toFloat()
            insn is LdcInsnNode && insn.cst is Float -> insn.cst as Float
            else -> throw IllegalArgumentException("Instruction can't return float")
        }
    }

    fun getInsn(float: Float): AbstractInsnNode {
        return when {
            float % 1f == 0f && float >= 0f && float <= 3f -> InsnNode(float.toInt() + 11)
            else -> LdcInsnNode(float)
        }
    }

    /* ------------------ Double Utilities ------------------ */

    fun getDouble(insn: AbstractInsnNode): Double {
        return when {
            insn.opcode in DCONST_0..DCONST_1 -> (insn.opcode - 14).toDouble()
            insn is LdcInsnNode && insn.cst is Double -> insn.cst as Double
            else -> throw IllegalArgumentException("Instruction can't return double")
        }
    }

    fun getInsn(double: Double): AbstractInsnNode {
        return when {
            double % 1.0 == 0.0 && double >= 0.0 && double <= 0.0 -> InsnNode(double.toInt() + 14)
            else -> LdcInsnNode(double)
        }
    }

}