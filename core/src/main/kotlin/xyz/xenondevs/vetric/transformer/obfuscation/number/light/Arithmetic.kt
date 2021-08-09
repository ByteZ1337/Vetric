package xyz.xenondevs.vetric.transformer.obfuscation.number.light

import org.objectweb.asm.tree.AbstractInsnNode
import xyz.xenondevs.vetric.transformer.TransformerPriority.NORMAL
import xyz.xenondevs.vetric.transformer.obfuscation.number.NumberTransformer
import xyz.xenondevs.vetric.util.asm.ASMUtils.InsnParent
import xyz.xenondevs.vetric.util.asm.insnBuilder
import xyz.xenondevs.vetric.util.asm.replace
import xyz.xenondevs.vetric.util.nextDouble
import xyz.xenondevs.vetric.util.nextFloat
import xyz.xenondevs.vetric.util.repeatRandom
import kotlin.random.Random

object Arithmetic : NumberTransformer("Arithmetic", NORMAL, true) {
    
    override fun transformInteger(insnParent: InsnParent, insn: AbstractInsnNode, value: Int) {
        val startValue = Random.nextInt()
        var current = startValue
        insnParent.insnList.replace(insn, insnBuilder {
            ldc(startValue)
            repeatRandom(1..3) {
                val operand = Random.nextInt()
                ldc(operand)
                if (Random.nextBoolean()) {
                    current += operand
                    iadd()
                } else {
                    current -= operand
                    isub()
                }
            }
            ldc(value - current)
            iadd()
        })
    }
    
    override fun transformLong(insnParent: InsnParent, insn: AbstractInsnNode, value: Long) {
        val startValue = Random.nextLong()
        var current = startValue
        insnParent.insnList.replace(insn, insnBuilder {
            ldc(startValue)
            repeatRandom(1..3) {
                val operand = Random.nextLong()
                ldc(operand)
                if (Random.nextBoolean()) {
                    current += operand
                    ladd()
                } else {
                    current -= operand
                    lsub()
                }
            }
            ldc(value - current)
            ladd()
        })
    }
    
    override fun transformFloat(insnParent: InsnParent, insn: AbstractInsnNode, value: Float) {
        val startValue = Random.nextFloat(-500000..500000)
        var current = startValue
        insnParent.insnList.replace(insn, insnBuilder {
            ldc(startValue)
            repeatRandom(1..3) {
                val operand = Random.nextFloat(-500000..500000)
                ldc(operand)
                if (Random.nextBoolean()) {
                    current += operand
                    fadd()
                } else {
                    current -= operand
                    fsub()
                }
            }
            ldc(value - current)
            fadd()
        })
    }
    
    override fun transformDouble(insnParent: InsnParent, insn: AbstractInsnNode, value: Double) {
        val startValue = Random.nextDouble(-500000L..500000L)
        var current = startValue
        insnParent.insnList.replace(insn, insnBuilder {
            ldc(startValue)
            repeatRandom(1..3) {
                val operand = Random.nextDouble(-500000L..500000L)
                ldc(operand)
                if (Random.nextBoolean()) {
                    current += operand
                    dadd()
                } else {
                    current -= operand
                    dsub()
                }
            }
            ldc(value - current)
            dadd()
        })
    }
}