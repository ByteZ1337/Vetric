package xyz.xenondevs.obfuscator.asm.dump

import org.objectweb.asm.Label
import org.objectweb.asm.Opcodes.*
import org.objectweb.asm.tree.MethodNode
import xyz.xenondevs.obfuscator.util.StringUtils.randomString

object CryptDump {
    fun dump(methodName: String, method: MethodNode) {
        // Encrypt method
        method.access = ACC_PUBLIC or ACC_STATIC
        method.name = methodName
        method.desc = "(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;"
        method.signature = null
        method.exceptions = null
        method.visitCode()
        val label0 = Label()
        method.visitLabel(label0)
        method.visitLineNumber(6, label0)
        method.visitLdcInsn("")
        method.visitVarInsn(ASTORE, 2)
        val label1 = Label()
        method.visitLabel(label1)
        method.visitLineNumber(7, label1)
        method.visitInsn(ICONST_0)
        method.visitVarInsn(ISTORE, 3)
        val label2 = Label()
        method.visitLabel(label2)
        method.visitFrame(F_APPEND, 2, arrayOf<Any>("java/lang/String", INTEGER), 0, null)
        method.visitVarInsn(ILOAD, 3)
        method.visitVarInsn(ALOAD, 0)
        method.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "length", "()I", false)
        val label3 = Label()
        method.visitJumpInsn(IF_ICMPGE, label3)
        val label4 = Label()
        method.visitLabel(label4)
        method.visitLineNumber(8, label4)
        method.visitTypeInsn(NEW, "java/lang/StringBuilder")
        method.visitInsn(DUP)
        method.visitMethodInsn(INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "()V", false)
        method.visitVarInsn(ALOAD, 2)
        method.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false)
        method.visitVarInsn(ALOAD, 0)
        method.visitVarInsn(ILOAD, 3)
        method.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "charAt", "(I)C", false)
        method.visitInsn(I2B)
        method.visitVarInsn(ALOAD, 1)
        method.visitVarInsn(ILOAD, 3)
        method.visitVarInsn(ALOAD, 1)
        method.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "length", "()I", false)
        method.visitInsn(IREM)
        method.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "charAt", "(I)C", false)
        method.visitInsn(I2B)
        method.visitInsn(IXOR)
        method.visitInsn(I2C)
        method.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(C)Ljava/lang/StringBuilder;", false)
        method.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;", false)
        method.visitVarInsn(ASTORE, 2)
        val label5 = Label()
        method.visitLabel(label5)
        method.visitLineNumber(7, label5)
        method.visitIincInsn(3, 1)
        method.visitJumpInsn(GOTO, label2)
        method.visitLabel(label3)
        method.visitLineNumber(10, label3)
        method.visitFrame(F_CHOP, 1, null, 0, null)
        method.visitVarInsn(ALOAD, 2)
        method.visitInsn(ARETURN)
        val label6 = Label()
        method.visitLabel(label6)
        method.visitLocalVariable(randomString(5..30), "I", null, label2, label3, 3)
        method.visitLocalVariable(randomString(5..30), "Ljava/lang/String;", null, label0, label6, 0)
        method.visitLocalVariable(randomString(5..30), "Ljava/lang/String;", null, label0, label6, 1)
        method.visitLocalVariable(randomString(5..30), "Ljava/lang/String;", null, label1, label6, 2)
        method.visitMaxs(5, 4)
        method.visitEnd()
    }
}