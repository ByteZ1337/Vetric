package xyz.xenondevs.obfuscator.util

import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassReader.EXPAND_FRAMES
import org.objectweb.asm.Opcodes.*
import org.objectweb.asm.Type
import org.objectweb.asm.tree.*

object AsmUtils {

    /* ------------------ Access Checks ------------------ */

    fun isPublic(access: Int) = access and ACC_PUBLIC != 0

    fun isPrivate(access: Int) = access and ACC_PRIVATE != 0

    fun isProtected(access: Int) = access and ACC_PROTECTED != 0

    fun isStatic(access: Int) = access and ACC_STATIC != 0

    fun isFinal(access: Int) = access and ACC_FINAL != 0

    fun isAbstract(access: Int) = access and ACC_ABSTRACT != 0

    fun isEnum(access: Int) = access and ACC_ENUM != 0

    fun isInterface(access: Int) = access and ACC_INTERFACE != 0

    /* ------------------ General Utilities ------------------ */

    fun isReturn(opcode: Int) = opcode in IRETURN..RETURN

    fun buildMethodDesc(returnType: Type, params: List<Type>): String =
            "(${params.joinToString("") { it.descriptor }})${returnType.descriptor}"

    inline fun <reified T> asClassNode() =
            asClassNode(T::class.java.canonicalName)

    fun asClassNode(name: String): ClassNode =
            ClassNode().also { ClassReader(name).accept(it, EXPAND_FRAMES) }


    fun getParent(method: MethodNode, clazz: ClassNode): ClassNode? {

        if (clazz.superName != null) {
            val superClass = asClassNode(clazz.superName)
            val rec = getParent(method, superClass)
            if (hasMethod(method.name, method.desc, superClass) && rec == null)
                return superClass
            if (rec != null)
                return rec
        }
        if (clazz.interfaces != null) {
            clazz.interfaces.forEach { interf ->
                val interfaceClass = asClassNode(interf)
                val rec = getParent(method, interfaceClass)
                if (hasMethod(method.name, method.desc, interfaceClass) && rec == null)
                    return interfaceClass
                if (rec != null)
                    return rec
            }
        }
        return null
    }

    fun isInherited(method: MethodNode, clazz: ClassNode) =
            getParent(method, clazz) != null

    fun hasMethod(name: String, desc: String, clazz: ClassNode) =
            clazz.methods != null && clazz.methods.any { it.desc == desc && it.name == name }

    /* ------------------ Int Utilities ------------------ */

    fun isIntInsn(insn: AbstractInsnNode) = when {
        insn.opcode in ICONST_M1..ICONST_5 -> true
        insn is IntInsnNode -> true
        insn is LdcInsnNode && insn.cst is Int -> true
        else -> false
    }

    fun getInt(insn: AbstractInsnNode) = when {
        insn.opcode in ICONST_M1..ICONST_5 -> insn.opcode - 3
        insn is IntInsnNode -> insn.operand
        insn is LdcInsnNode && insn.cst is Int -> insn.cst as Int
        else -> throw IllegalArgumentException("Instruction can't return int")
    }

    fun getInsn(int: Int) = when (int) {
        in 0..5 -> InsnNode(int + 3)
        in -128..127 -> IntInsnNode(BIPUSH, int)
        in -32768..32767 -> IntInsnNode(SIPUSH, int)
        else -> LdcInsnNode(int)
    }

    /* ------------------ Long Utilities ------------------ */

    fun getLong(insn: AbstractInsnNode) = when {
        insn.opcode in LCONST_0..LCONST_1 -> (insn.opcode - 9).toLong()
        insn is LdcInsnNode && insn.cst is Long -> insn.cst as Long
        else -> throw IllegalArgumentException("Instruction can't return long")
    }

    fun getInsn(long: Long) = when (long) {
        in 0L..1L -> InsnNode(long.toInt() + 9)
        else -> LdcInsnNode(long)
    }

    /* ------------------ Float Utilities ------------------ */

    fun getFloat(insn: AbstractInsnNode) = when {
        insn.opcode in FCONST_0..FCONST_2 -> (insn.opcode - 11).toFloat()
        insn is LdcInsnNode && insn.cst is Float -> insn.cst as Float
        else -> throw IllegalArgumentException("Instruction can't return float")
    }

    fun getInsn(float: Float) = when {
        float % 1f == 0f && float >= 0f && float <= 3f -> InsnNode(float.toInt() + 11)
        else -> LdcInsnNode(float)
    }

    /* ------------------ Double Utilities ------------------ */

    fun getDouble(insn: AbstractInsnNode) = when {
        insn.opcode in DCONST_0..DCONST_1 -> (insn.opcode - 14).toDouble()
        insn is LdcInsnNode && insn.cst is Double -> insn.cst as Double
        else -> throw IllegalArgumentException("Instruction can't return double")
    }

    fun getInsn(double: Double) = when {
        double % 1.0 == 0.0 && double >= 0.0 && double <= 1.0 -> InsnNode(double.toInt() + 14)
        else -> LdcInsnNode(double)
    }

}