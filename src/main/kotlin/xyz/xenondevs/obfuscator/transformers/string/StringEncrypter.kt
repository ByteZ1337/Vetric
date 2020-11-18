package xyz.xenondevs.obfuscator.transformers.string

import org.objectweb.asm.Label
import org.objectweb.asm.Opcodes.*
import org.objectweb.asm.tree.LdcInsnNode
import org.objectweb.asm.tree.MethodInsnNode
import org.objectweb.asm.tree.MethodNode
import xyz.xenondevs.obfuscator.jvm.ClassWrapper
import xyz.xenondevs.obfuscator.jvm.JavaArchive
import xyz.xenondevs.obfuscator.utils.ASMUtils
import xyz.xenondevs.obfuscator.utils.StringUtils
import kotlin.math.ln
import kotlin.math.roundToInt

object StringEncrypter : StringTransformer("StringEncrypter", { it.length <= 1000 }) {

    val methods = ArrayList<Pair<String, String>>()

    override fun transformJar(jar: JavaArchive) {
        // Clear methods to make sure old methods aren't used
        methods.clear()

        val size = jar.classes.count(this::isInjectable)
        if (size == 0) {
            System.err.println("Not enough public classes for decryption method. Skipping $name")
            return
        }
        val amount = (ln(size.toDouble()) * 2).roundToInt().coerceIn(1..10)
        // Shuffling the classes list and taking random accessible non interface ClassWrappers
        jar.classes.filter(this::isInjectable).shuffled().take(amount).forEach {
            val method = generateDecryptMethod()
            it.methods.add(method)
            methods += it.name to method.name
        }
        println("Decrypt methods in:\n" + methods.joinToString("\n") { it.first })
        super.transformJar(jar)
    }

    override fun transformString(method: MethodNode, instruction: LdcInsnNode, string: String) {
        if (methods.any { it.first == current.name && it.second == method.name })
            return

        println("Encrypting ${instruction.cst}")
        val key = LdcInsnNode(StringUtils.randomString(10..20))
        instruction.cst = StringUtils.encrypt(string, key.cst as String)
        method.instructions.insert(instruction, key)
        val randomMethod = methods.random()
        method.instructions.insert(
            key,
            MethodInsnNode(
                INVOKESTATIC,
                randomMethod.first,
                randomMethod.second,
                "(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;"
            )
        )
    }

    fun isInjectable(wrapper: ClassWrapper) =
        !wrapper.isInterface() && !wrapper.isEnum() && ASMUtils.isPublic(wrapper.access)

    fun generateDecryptMethod(): MethodNode {
        val method = MethodNode()
        with(method) {
            access = ACC_PUBLIC or ACC_STATIC
            name = "decrypt" + StringUtils.randomString(5..10)
            desc = "(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;"
            signature = null
            exceptions = null
            visitCode()
            val label0 = Label()
            visitLabel(label0)
            visitLdcInsn("")
            visitVarInsn(ASTORE, 2)
            val label1 = Label()
            visitLabel(label1)
            visitInsn(ICONST_0)
            visitVarInsn(ISTORE, 3)
            val label2 = Label()
            visitLabel(label2)
            visitFrame(F_APPEND, 2, arrayOf<Any>("java/lang/String", INTEGER), 0, null)
            visitVarInsn(ILOAD, 3)
            visitVarInsn(ALOAD, 0)
            visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "length", "()I", false)
            val label3 = Label()
            visitJumpInsn(IF_ICMPGE, label3)
            val label4 = Label()
            visitLabel(label4)
            visitTypeInsn(NEW, "java/lang/StringBuilder")
            visitInsn(DUP)
            visitMethodInsn(INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "()V", false)
            visitVarInsn(ALOAD, 2)
            visitMethodInsn(
                INVOKEVIRTUAL,
                "java/lang/StringBuilder",
                "append",
                "(Ljava/lang/String;)Ljava/lang/StringBuilder;",
                false
            )
            visitVarInsn(ALOAD, 0)
            visitVarInsn(ILOAD, 3)
            visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "charAt", "(I)C", false)
            visitVarInsn(ALOAD, 1)
            visitVarInsn(ILOAD, 3)
            visitVarInsn(ALOAD, 1)
            visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "length", "()I", false)
            visitInsn(IREM)
            visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "charAt", "(I)C", false)
            visitInsn(IXOR)
            visitInsn(I2C)
            visitMethodInsn(
                INVOKEVIRTUAL,
                "java/lang/StringBuilder",
                "append",
                "(C)Ljava/lang/StringBuilder;",
                false
            )
            visitMethodInsn(
                INVOKEVIRTUAL,
                "java/lang/StringBuilder",
                "toString",
                "()Ljava/lang/String;",
                false
            )
            visitVarInsn(ASTORE, 2)
            val label5 = Label()
            visitLabel(label5)
            visitIincInsn(3, 1)
            visitJumpInsn(GOTO, label2)
            visitLabel(label3)
            visitFrame(F_CHOP, 1, null, 0, null)
            visitVarInsn(ALOAD, 2)
            visitInsn(ARETURN)
            val label6 = Label()
            visitLabel(label6)
            visitLocalVariable(StringUtils.randomString(5..30), "I", null, label2, label3, 3)
            visitLocalVariable(StringUtils.randomString(5..30), "Ljava/lang/String;", null, label0, label6, 0)
            visitLocalVariable(StringUtils.randomString(5..30), "Ljava/lang/String;", null, label0, label6, 1)
            visitLocalVariable(StringUtils.randomString(5..30), "Ljava/lang/String;", null, label1, label6, 2)
            visitMaxs(5, 4)
            visitEnd()
        }
        return method
    }

}