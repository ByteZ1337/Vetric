package xyz.xenondevs.obfuscator.transformer.number

import org.objectweb.asm.Opcodes.*
import org.objectweb.asm.tree.FieldNode
import org.objectweb.asm.tree.InsnList
import org.objectweb.asm.tree.InsnNode
import org.objectweb.asm.tree.MethodNode
import xyz.xenondevs.obfuscator.asm.SmartClass
import xyz.xenondevs.obfuscator.transformer.ClassTransformer
import xyz.xenondevs.obfuscator.util.AsmUtils
import xyz.xenondevs.obfuscator.util.MathUtils.randomInt

@ExperimentalStdlibApi
class LogicalConverter : ClassTransformer("Logical Converter") {

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
        repeat((0..randomInt(1..2)).count()) {
            randomInt().run {
                instructions.add(AsmUtils.getInsn(this))
                current = when (randomInt(0..2)) {
                    0 -> {
                        instructions.add(InsnNode(IOR))
                        current or this
                    }
                    1 -> {
                        instructions.add(InsnNode(IAND))
                        current and this
                    }
                    else -> {
                        instructions.add(InsnNode(IXOR))
                        current xor this
                    }
                }
            }
        }
        instructions.add(AsmUtils.getInsn(current xor number))
        instructions.add(InsnNode(IXOR))
        return instructions
    }

    override fun transform(smartClass: SmartClass) {
    }

    override fun transform(field: FieldNode) {
    }
}