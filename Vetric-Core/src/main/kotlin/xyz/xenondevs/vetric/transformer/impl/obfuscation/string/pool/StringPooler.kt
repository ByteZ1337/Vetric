package xyz.xenondevs.vetric.transformer.impl.obfuscation.string.pool

import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.FieldNode
import org.objectweb.asm.tree.LdcInsnNode
import org.objectweb.asm.tree.MethodNode
import xyz.xenondevs.bytebase.asm.buildInsnList
import xyz.xenondevs.bytebase.jvm.ClassWrapper
import xyz.xenondevs.bytebase.util.replace
import xyz.xenondevs.vetric.supplier.DEFAULT_SUPPLIER
import xyz.xenondevs.vetric.transformer.TransformerPriority
import xyz.xenondevs.vetric.transformer.impl.obfuscation.string.StringTransformer
import xyz.xenondevs.vetric.utils.filterTypeSub
import xyz.xenondevs.vetric.utils.getStringPool

// FIXME: interfaces
object StringPooler : StringTransformer("StringPooler", TransformerPriority.LOWEST) {
    
    val supplier = DEFAULT_SUPPLIER
    
    override fun transform(clazz: ClassWrapper) {
        val supp = supplier.create(2)
        val strings = clazz.getStringPool()
        if(strings.isEmpty()) return
        
        val poolField = FieldNode(Opcodes.ACC_PUBLIC or Opcodes.ACC_STATIC, supp.randomString(), "[Ljava/lang/String;", null, null)
        val poolFieldInit = MethodNode(Opcodes.ACC_PUBLIC or Opcodes.ACC_STATIC, supp.randomString(), "()[Ljava/lang/String;", null, null)
        val arrayIndices = HashMap<String, Int>()
        clazz.fields.add(poolField)
        clazz.methods.add(poolFieldInit)
        poolFieldInit.instructions = buildInsnList {
            ldc(strings.size)
            aNewArray("java/lang/String")
            strings.forEachIndexed { index, string ->
                arrayIndices[string] = index
                dup()
                ldc(index)
                ldc(string)
                aastore()
            }
            areturn()
        }
        clazz.getOrCreateClassInit().instructions.insert(buildInsnList {
            invokeStatic(clazz.name, poolFieldInit.name, "()[Ljava/lang/String;")
            putStatic(clazz.name, poolField.name, "[Ljava/lang/String;")
        })
        replaceStrings(clazz, arrayIndices, poolField, poolFieldInit)
    }
    
    internal fun replaceStrings(clazz: ClassWrapper, arrayIndices: HashMap<String, Int>, poolField: FieldNode, poolFieldInit: MethodNode) {
        clazz.methods.forEach {
            if (it === poolFieldInit) return@forEach
            val insnList = it.instructions
            insnList.filterTypeSub<LdcInsnNode, String>(LdcInsnNode::cst).forEach { insn ->
                val value = insn.cst as String
                insnList.replace(insn, buildInsnList {
                    getStatic(clazz.name, poolField.name, "[Ljava/lang/String;")
                    ldc(arrayIndices[value]!!)
                    aaload()
                })
            }
        }
    }
    
}