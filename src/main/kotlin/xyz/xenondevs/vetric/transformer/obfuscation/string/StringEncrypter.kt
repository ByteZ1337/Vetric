package xyz.xenondevs.vetric.transformer.obfuscation.string

import org.objectweb.asm.Opcodes.*
import org.objectweb.asm.tree.LabelNode
import org.objectweb.asm.tree.LdcInsnNode
import org.objectweb.asm.tree.MethodInsnNode
import org.objectweb.asm.tree.MethodNode
import xyz.xenondevs.vetric.config.type.TransformerConfig
import xyz.xenondevs.vetric.exclusion.ExclusionManager
import xyz.xenondevs.vetric.jvm.ClassWrapper
import xyz.xenondevs.vetric.jvm.JavaArchive
import xyz.xenondevs.vetric.supplier.CombiningSupplier
import xyz.xenondevs.vetric.util.StringUtils
import xyz.xenondevs.vetric.util.asm.ASMUtils.MemberReference
import xyz.xenondevs.vetric.util.asm.insnBuilder
import kotlin.math.ln
import kotlin.math.roundToInt

object StringEncrypter : StringTransformer("StringEncrypter", TransformerConfig(StringEncrypter::class), { it.length <= 1000 }) {
    
    private val methods = ArrayList<MemberReference>()
    private val supplier = CombiningSupplier()
    
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
            methods += MemberReference(it.name, method.name, method.desc)
        }
        println("Decrypt methods in:\n" + methods.joinToString("\n"))
        super.transformJar(jar)
    }
    
    override fun transformString(method: MethodNode, instruction: LdcInsnNode, string: String) {
        if (methods.any { it.owner == currentClass.name && it.name == method.name })
            return
        
        println("Encrypting ${instruction.cst}")
        val key = LdcInsnNode(supplier.randomString(10..20))
        instruction.cst = StringUtils.encrypt(string, key.cst as String)
        method.instructions.insert(instruction, key)
        val randomMethod = methods.random()
        method.instructions.insert(
            key,
            MethodInsnNode(
                INVOKESTATIC,
                randomMethod.owner,
                randomMethod.name,
                randomMethod.desc
            )
        )
    }
    
    private fun isInjectable(clazz: ClassWrapper) =
        clazz.accessWrapper.isPublicClass() && !ExclusionManager.isExcluded(clazz)
    
    // TODO generate in Runtime
    private fun generateDecryptMethod(): MethodNode {
        val method = MethodNode()
        with(method) {
            access = ACC_PUBLIC or ACC_STATIC
            name = "decrypt" + supplier.randomString(5..10)
            desc = "(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;"
            signature = null
            exceptions = null
            maxStack = 5
            maxLocals = 4
            instructions = insnBuilder {
                ldc("")
                astore(2)
                ldc(0)
                istore(3)
                val label2 = LabelNode()
                +label2
                iload(3)
                aload(0)
                invokevirtual("java/lang/String", "length", "()I")
                val label3 = LabelNode()
                if_icmpge(label3)
                new("java/lang/StringBuilder")
                dup()
                invokespecial("java/lang/StringBuilder", "<init>", "()V")
                aload(2)
                invokevirtual("java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;")
                aload(0)
                iload(3)
                invokevirtual("java/lang/String", "charAt", "(I)C")
                aload(1)
                dup()
                iload(3)
                swap()
                invokevirtual("java/lang/String", "length", "()I")
                irem()
                invokevirtual("java/lang/String", "charAt", "(I)C")
                visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "charAt", "(I)C", false)
                ixor()
                i2c()
                invokevirtual("java/lang/StringBuilder", "append", "(C)Ljava/lang/StringBuilder;")
                invokevirtual("java/lang/StringBuilder", "toString", "()Ljava/lang/String;")
                astore(2)
                iinc(3, 1)
                goto(label2)
                +label3
                aload(2)
                areturn()
            }
        }
        return method
    }
}