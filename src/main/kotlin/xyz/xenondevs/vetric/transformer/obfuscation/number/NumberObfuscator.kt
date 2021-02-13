package xyz.xenondevs.vetric.transformer.obfuscation.number

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import org.objectweb.asm.Opcodes.*
import org.objectweb.asm.tree.AbstractInsnNode
import org.objectweb.asm.tree.FieldNode
import org.objectweb.asm.tree.LdcInsnNode
import org.objectweb.asm.tree.MethodNode
import xyz.xenondevs.vetric.config.type.TransformerConfig
import xyz.xenondevs.vetric.jvm.ClassWrapper
import xyz.xenondevs.vetric.transformer.ClassTransformer
import xyz.xenondevs.vetric.transformer.TransformerPriority.LOW
import xyz.xenondevs.vetric.transformer.obfuscation.number.light.Arithmetic
import xyz.xenondevs.vetric.transformer.obfuscation.number.light.Bitwise
import xyz.xenondevs.vetric.transformer.obfuscation.number.light.Xor
import xyz.xenondevs.vetric.transformer.obfuscation.number.medium.Encoder
import xyz.xenondevs.vetric.util.asm.ASMUtils
import xyz.xenondevs.vetric.util.asm.ASMUtils.InsnParent
import xyz.xenondevs.vetric.util.json.*

object NumberObfuscator : ClassTransformer("NumberObfuscator", NumberObfuscatorConfig, LOW) {
    
    private val transformers = sortedSetOf(
        Xor, Bitwise, Arithmetic, Encoder
    )
    
    fun getTransformer(name: String): NumberTransformer? = transformers.firstOrNull { it.name.equals(name, true) }
    
    override fun transformMethod(method: MethodNode) {
        val enabled = transformers.filter(NumberTransformer::enabled)
        val maxIterations = enabled.maxOf(NumberTransformer::iterations)
        
        repeat(maxIterations) { iteration ->
            enabled.filter { it.iterations > iteration }.forEach { transformer ->
                method.instructions.forEach insnLoop@{ insn ->
                    val number = when {
                        insn is LdcInsnNode && insn.cst is Number -> insn.cst as Number
                        insn.opcode in ICONST_0..ICONST_5 || insn.opcode in BIPUSH..SIPUSH -> ASMUtils.getInt(insn)
                        insn.opcode in LCONST_0..LCONST_1 -> ASMUtils.getLong(insn)
                        insn.opcode in FCONST_0..FCONST_2 -> ASMUtils.getFloat(insn)
                        insn.opcode in DCONST_0..DCONST_1 -> ASMUtils.getDouble(insn)
                        else -> return@insnLoop
                    }
                    callTransformer(transformer, method, insn, number)
                }
            }
        }
    }
    
    fun callTransformer(transformer: NumberTransformer, method: MethodNode, insn: AbstractInsnNode, value: Number) {
        val parent = InsnParent(currentJar, currentClass, method, method.instructions)
        
        when (value) {
            is Int -> transformer.transformInteger(parent, insn, value)
            is Long -> transformer.transformLong(parent, insn, value)
            is Float -> transformer.transformFloat(parent, insn, value)
            is Double -> transformer.transformDouble(parent, insn, value)
        }
    }
    
    override fun transformClass(clazz: ClassWrapper) = Unit
    
    override fun transformField(field: FieldNode) = Unit
    
    
    object NumberObfuscatorConfig : TransformerConfig(NumberObfuscator::class) {
        
        override fun parse(obj: JsonObject) {
            super.parse(obj)
            if (!enabled)
                return
            if (obj.hasArray("transformers"))
                obj.getAsJsonArray("transformers").forEachIndexed { index, element ->
                    handleTransformer(element, index)
                }
            if (transformers.none(NumberTransformer::enabled)) {
                println("No number transformers enabled! Disabling number obfuscator.")
                enabled = false
            }
        }
        
        private fun handleTransformer(element: JsonElement, index: Int) {
            run {
                if (element.isString()) {
                    val transformer = getTransformer(element.asString) ?: return@run
                    transformer.enabled = true
                    return
                } else if (element is JsonObject && element.hasString("name")) {
                    val transformer = getTransformer(element.getString("name")!!) ?: return@run
                    transformer.enabled = element.getBoolean("enabled", true)
                    if (transformer.multipleIterations)
                        transformer.iterations = element.getInt("iterations", 1)!!
                    return
                } else return@run
            }
            println("Invalid number transformer at index $index. Skipping...")
        }
        
    }
    
}