package xyz.xenondevs.obfuscator.transformer.string

import org.objectweb.asm.Opcodes.INVOKESTATIC
import org.objectweb.asm.tree.FieldNode
import org.objectweb.asm.tree.LdcInsnNode
import org.objectweb.asm.tree.MethodInsnNode
import org.objectweb.asm.tree.MethodNode
import xyz.xenondevs.obfuscator.asm.SmartClass
import xyz.xenondevs.obfuscator.transformer.ClassTransformer
import xyz.xenondevs.obfuscator.util.CryptUtils
import xyz.xenondevs.obfuscator.util.StringUtils

@ExperimentalStdlibApi
class StringEncrypter : ClassTransformer("StringEncrypter") {

    override fun transform(method: MethodNode) {
        method.instructions.forEach { insn ->
            run {
                if (insn is LdcInsnNode && insn.cst is String && !EncryptionInjector.methods.values.contains(method.name)) {
                    println("Encrypting ${insn.cst}")
                    val key = StringUtils.randomString(20..40)
                    val keyNode = LdcInsnNode(key)
                    insn.cst = CryptUtils.encrypt(insn.cst as String, key)
                    method.instructions.insert(insn, keyNode)
                    val rdmClass = EncryptionInjector.methods.keys.random()
                    method.instructions.insert(keyNode, MethodInsnNode(INVOKESTATIC, rdmClass, EncryptionInjector.methods[rdmClass], "(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;"))
                }
            }
        }
    }

    override fun transform(smartClass: SmartClass) = Unit

    override fun transform(field: FieldNode) = Unit
}
