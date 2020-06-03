package xyz.xenondevs.obfuscator.transformer.number

import org.objectweb.asm.Opcodes.IADD
import org.objectweb.asm.Opcodes.ISUB
import org.objectweb.asm.tree.FieldNode
import org.objectweb.asm.tree.InsnList
import org.objectweb.asm.tree.InsnNode
import org.objectweb.asm.tree.MethodNode
import xyz.xenondevs.obfuscator.asm.SmartClass
import xyz.xenondevs.obfuscator.transformer.ClassTransformer
import xyz.xenondevs.obfuscator.util.AsmUtils
import xyz.xenondevs.obfuscator.util.MathUtils.randomInt

@ExperimentalStdlibApi
class ArithmeticConverter : ClassTransformer("Arithmetic Converter") {
    override fun transform(method: MethodNode) {
        method.instructions.forEach {
            run {
                if (AsmUtils.isIntInsn(it)) {
                    val current = AsmUtils.getInt(it)
                    method.instructions.insertBefore(it, obfuscateNumber(current))
                    method.instructions.remove(it)
                }
            }
        }
    }

    fun obfuscateNumber(number: Int): InsnList {
        var current = randomInt()
        val instructions = InsnList()
        instructions.add(AsmUtils.getInsn(current))
        repeat((0..randomInt(0..1)).count()) {
            randomInt().run {
                instructions.add(AsmUtils.getInsn(this))
                current = when (randomInt(0..1)) {
                    0 -> {
                        instructions.add(InsnNode(IADD))
                        current + this
                    }
                    else -> {
                        instructions.add(InsnNode(ISUB))
                        current - this
                    }
                }
            }
        }
        instructions.add(AsmUtils.getInsn(number - current))
        instructions.add(InsnNode(IADD))
        return instructions
    }

    override fun transform(smartClass: SmartClass) {
    }

    override fun transform(field: FieldNode) {
    }
}