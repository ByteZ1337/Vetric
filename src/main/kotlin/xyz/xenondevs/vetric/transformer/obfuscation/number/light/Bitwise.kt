package xyz.xenondevs.vetric.transformer.obfuscation.number.light

import org.objectweb.asm.tree.AbstractInsnNode
import xyz.xenondevs.vetric.transformer.TransformerPriority
import xyz.xenondevs.vetric.transformer.TransformerPriority.HIGHEST
import xyz.xenondevs.vetric.transformer.obfuscation.number.NumberTransformer
import xyz.xenondevs.vetric.util.asm.ASMUtils.InsnParent
import xyz.xenondevs.vetric.util.asm.insnBuilder
import xyz.xenondevs.vetric.util.repeatRandom
import xyz.xenondevs.vetric.util.replace
import kotlin.random.Random
import kotlin.random.nextInt
import kotlin.random.nextLong

object Bitwise : NumberTransformer("Bitwise", HIGHEST, true) {
    
    override fun transformInteger(insnParent: InsnParent, insn: AbstractInsnNode, value: Int) {
        val startValue = Random.nextInt()
        var current = startValue
        insnParent.insnList.replace(insn, insnBuilder {
            ldc(startValue)
            repeatRandom(1..3) {
                val operation = Random.nextInt(1..6)
                val operand = if (operation < 4) Random.nextInt() else Random.nextInt(1..8)
                ldc(operand)
                when (operation) {
                    1 -> {
                        ior()
                        current = current or operand
                    }
                    2 -> {
                        iand()
                        current = current and operand
                    }
                    3 -> {
                        ixor()
                        current = current xor operand
                    }
                    4 -> {
                        ishr()
                        current = current shr operand
                    }
                    5 -> {
                        iushr()
                        current = current ushr operand
                    }
                    6 -> {
                        ishl()
                        current = current shl operand
                    }
                }
            }
            ldc(value xor current)
            ixor()
        })
    }
    
    override fun transformLong(insnParent: InsnParent, insn: AbstractInsnNode, value: Long) {
        val startValue = Random.nextLong()
        var current = startValue
        insnParent.insnList.replace(insn, insnBuilder {
            ldc(startValue)
            repeatRandom(1..3) {
                val operation = Random.nextInt(1..6)
                val operand = if (operation < 4) Random.nextLong() else Random.nextLong(1L..16L)
                when (operation) {
                    1 -> {
                        ldc(operand)
                        lor()
                        current = current or operand
                    }
                    2 -> {
                        ldc(operand)
                        land()
                        current = current and operand
                    }
                    3 -> {
                        ldc(operand)
                        lxor()
                        current = current xor operand
                    }
                    4 -> {
                        ldc(operand.toInt())
                        lshr()
                        current = current shr operand.toInt()
                    }
                    5 -> {
                        ldc(operand.toInt())
                        lushr()
                        current = current ushr operand.toInt()
                    }
                    6 -> {
                        ldc(operand.toInt())
                        lshl()
                        current = current shl operand.toInt()
                    }
                }
            }
            ldc(value xor current)
            lxor()
        })
    }
}