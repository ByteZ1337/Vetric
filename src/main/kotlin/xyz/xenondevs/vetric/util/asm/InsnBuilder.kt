@file:Suppress("NOTHING_TO_INLINE", "FunctionName")

package xyz.xenondevs.vetric.util.asm

import org.objectweb.asm.Handle
import org.objectweb.asm.Opcodes.*
import org.objectweb.asm.Type
import org.objectweb.asm.tree.*
import xyz.xenondevs.vetric.util.internalName
import kotlin.reflect.KClass

/**
 * Generate [InsnLists][InsnList] without calling
 * tons of constructors by hand.
 *
 * [Original](https://github.com/x4e/PaperBin/blob/master/src/main/kotlin/dev/binclub/paperbin/utils/InsnBuilder.kt)
 * concept and codebase by cookiedragon234.
 *
 * @author cookiedragon234, ByteZ
 * @see <a href="https://en.wikipedia.org/wiki/Java_bytecode_instruction_listings">List of Bytecode instructions</a>
 */
class InsnBuilder {
    /**
     * The generated list.
     */
    val list = InsnList()
    
    /**
     * Adds the subsequent [InsnList] to the [list]
     */
    inline operator fun InsnList.unaryPlus() = list.add(this)
    
    /**
     * Adds the subsequent [AbstractInsnNode] to the [list]
     */
    inline operator fun AbstractInsnNode.unaryPlus() = list.add(this)
    
    /**
     * Adds println instructions for the given text
     *
     * @param text the text to be printed
     */
    fun print(text: String) {
        getstatic("java/lang/System", "out", "Ljava/io/PrintStream;")
        ldc(text)
        invokevirtual("java/io/PrintStream", "println", "(Ljava/lang/String;)V")
    }
    
    /**
     * Creates a new [LabelNode]. To also
     * automatically add it use [addLabel].
     *
     * @return a new [LabelNode]
     */
    fun label() = LabelNode()
    
    /**
     * Creas a new [LabelNode] and adds
     * it to the [list].
     */
    fun addLabel() = +label()
    
    /**
     * Adds a [zero operand instruction][InsnNode] to the [list].
     *
     * @param opcode the opcode of the instruction
     */
    inline fun insn(opcode: Int) = +InsnNode(opcode)
    
    inline fun nop() = insn(NOP)
    
    /* Loading constant values */
    
    inline fun aconst_null() = insn(ACONST_NULL)
    inline fun ldc(int: Int) = +ASMUtils.getIntInsn(int)
    inline fun ldc(long: Long) = +ASMUtils.getLongInsn(long)
    inline fun ldc(float: Float) = +ASMUtils.getFloatInsn(float)
    inline fun ldc(double: Double) = +ASMUtils.getDoubleInsn(double)
    inline fun ldc(string: String) = +LdcInsnNode(string)
    inline fun ldc(type: Type) = +LdcInsnNode(type)
    inline fun ldc(handle: Handle) = +LdcInsnNode(handle)
    
    /* Locals */
    
    inline fun istore(`var`: Int) = +VarInsnNode(ISTORE, `var`)
    inline fun iload(`var`: Int) = +VarInsnNode(ILOAD, `var`)
    inline fun lstore(`var`: Int) = +VarInsnNode(LSTORE, `var`)
    inline fun lload(`var`: Int) = +VarInsnNode(LLOAD, `var`)
    inline fun fstore(`var`: Int) = +VarInsnNode(FSTORE, `var`)
    inline fun fload(`var`: Int) = +VarInsnNode(FLOAD, `var`)
    inline fun dstore(`var`: Int) = +VarInsnNode(DSTORE, `var`)
    inline fun dload(`var`: Int) = +VarInsnNode(DLOAD, `var`)
    inline fun astore(`var`: Int) = +VarInsnNode(ASTORE, `var`)
    inline fun aload(`var`: Int) = +VarInsnNode(ALOAD, `var`)
    
    /* Array storing & loading */
    
    inline fun iastore() = insn(IASTORE)
    inline fun iaload() = insn(IALOAD)
    inline fun lastore() = insn(LASTORE)
    inline fun laload() = insn(LALOAD)
    inline fun fastore() = insn(FASTORE)
    inline fun faload() = insn(FALOAD)
    inline fun dastore() = insn(DASTORE)
    inline fun daload() = insn(DALOAD)
    inline fun aastore() = insn(AASTORE)
    inline fun aaload() = insn(AALOAD)
    inline fun bastore() = insn(BASTORE)
    inline fun baload() = insn(BALOAD)
    inline fun castore() = insn(CASTORE)
    inline fun caload() = insn(CALOAD)
    inline fun sastore() = insn(SASTORE)
    inline fun saload() = insn(SALOAD)
    
    /* Stack manipulation */
    
    inline fun pop() = insn(POP)
    inline fun pop2() = insn(POP2)
    inline fun dup() = insn(DUP)
    inline fun dup_x1() = insn(DUP_X1)
    inline fun dup_x2() = insn(DUP_X2)
    inline fun dup2() = insn(DUP2)
    inline fun dup2_x1() = insn(DUP2_X1)
    inline fun dup2_x2() = insn(DUP2_X2)
    inline fun swap() = insn(SWAP)
    
    /* Arithmetic & Bitwise */
    
    inline fun iadd() = insn(IADD)
    inline fun isub() = insn(ISUB)
    inline fun imul() = insn(IMUL)
    inline fun idiv() = insn(IDIV)
    inline fun irem() = insn(IREM)
    inline fun ineg() = insn(INEG)
    inline fun ishl() = insn(ISHL)
    inline fun ishr() = insn(ISHR)
    inline fun iushr() = insn(IUSHR)
    inline fun iand() = insn(IAND)
    inline fun ior() = insn(IOR)
    inline fun ixor() = insn(IXOR)
    inline fun iinc(`var`: Int, incr: Int) = +IincInsnNode(`var`, incr)
    
    inline fun ladd() = insn(LADD)
    inline fun lsub() = insn(LSUB)
    inline fun lmul() = insn(LMUL)
    inline fun ldiv() = insn(LDIV)
    inline fun lrem() = insn(LREM)
    inline fun lneg() = insn(LNEG)
    inline fun lshl() = insn(LSHL)
    inline fun lshr() = insn(LSHR)
    inline fun lushr() = insn(LUSHR)
    inline fun lor() = insn(LOR)
    inline fun land() = insn(LAND)
    inline fun lxor() = insn(LXOR)
    
    inline fun fadd() = insn(FADD)
    inline fun fsub() = insn(FSUB)
    inline fun fmul() = insn(FMUL)
    inline fun fdiv() = insn(FDIV)
    inline fun frem() = insn(FREM)
    inline fun fneg() = insn(FNEG)
    
    inline fun dadd() = insn(DADD)
    inline fun dsub() = insn(DSUB)
    inline fun dmul() = insn(DMUL)
    inline fun ddiv() = insn(DDIV)
    inline fun drem() = insn(DREM)
    inline fun dneg() = insn(DNEG)
    
    /* Primitive type conversion */
    
    inline fun i2l() = insn(I2L)
    inline fun i2f() = insn(I2F)
    inline fun i2d() = insn(I2D)
    inline fun i2b() = insn(I2B)
    inline fun i2c() = insn(I2C)
    inline fun i2s() = insn(I2S)
    inline fun l2i() = insn(L2I)
    inline fun l2f() = insn(L2F)
    inline fun l2d() = insn(L2D)
    inline fun f2i() = insn(F2I)
    inline fun f2l() = insn(F2L)
    inline fun f2d() = insn(F2D)
    inline fun d2i() = insn(D2I)
    inline fun d2l() = insn(D2L)
    inline fun d2f() = insn(D2F)
    
    /* Number comparisons */
    
    inline fun lcmp() = insn(LCMP)
    inline fun fcmpl() = insn(FCMPL)
    inline fun fcmpg() = insn(FCMPG)
    inline fun dcmpl() = insn(DCMPL)
    inline fun dcmpg() = insn(DCMPG)
    
    /* Jumping */
    
    inline fun goto(label: LabelNode) = +JumpInsnNode(GOTO, label)
    inline fun jsr(label: LabelNode) = +JumpInsnNode(JSR, label)
    
    inline fun ifeq(label: LabelNode) = +JumpInsnNode(IFEQ, label)
    inline fun ifne(label: LabelNode) = +JumpInsnNode(IFNE, label)
    inline fun iflt(label: LabelNode) = +JumpInsnNode(IFLT, label)
    inline fun ifle(label: LabelNode) = +JumpInsnNode(IFLE, label)
    inline fun ifge(label: LabelNode) = +JumpInsnNode(IFGE, label)
    inline fun ifgt(label: LabelNode) = +JumpInsnNode(IFGT, label)
    
    inline fun if_icmplt(label: LabelNode) = +JumpInsnNode(IF_ICMPLT, label)
    inline fun if_icmple(label: LabelNode) = +JumpInsnNode(IF_ICMPLE, label)
    inline fun if_icmpge(label: LabelNode) = +JumpInsnNode(IF_ICMPGE, label)
    inline fun if_icmpgt(label: LabelNode) = +JumpInsnNode(IF_ICMPGT, label)
    inline fun if_icmpeq(label: LabelNode) = +JumpInsnNode(IF_ICMPEQ, label)
    inline fun if_icmpne(label: LabelNode) = +JumpInsnNode(IF_ICMPNE, label)
    
    inline fun ifnull(label: LabelNode) = +JumpInsnNode(IFNULL, label)
    inline fun ifnonnull(label: LabelNode) = +JumpInsnNode(IFNONNULL, label)
    
    // inline fun tableswitch(baseNumber: Int, dflt: LabelNode, vararg targets: LabelNode) = +buildTableSwitch(baseNumber, dflt, *targets)
    // inline fun lookupswitch(defaultLabel: LabelNode, lookup: Array<Pair<Int, LabelNode>>) = +buildLookupSwitch(defaultLabel, lookup)
    
    /* Fields */
    
    inline fun getstatic(owner: String, name: String, desc: String) = +FieldInsnNode(GETSTATIC, owner, name, desc)
    inline fun putstatic(owner: String, name: String, desc: String) = +FieldInsnNode(PUTSTATIC, owner, name, desc)
    inline fun getfield(owner: String, name: String, desc: String) = +FieldInsnNode(GETFIELD, owner, name, desc)
    inline fun putfield(owner: String, name: String, desc: String) = +FieldInsnNode(PUTFIELD, owner, name, desc)
    
    /* Method invocation */
    
    inline fun invokevirtual(owner: String, name: String, desc: String, `interface`: Boolean = false) = +MethodInsnNode(INVOKEVIRTUAL, owner, name, desc, `interface`)
    inline fun invokespecial(owner: String, name: String, desc: String, `interface`: Boolean = false) = +MethodInsnNode(INVOKESPECIAL, owner, name, desc, `interface`)
    inline fun invokestatic(owner: String, name: String, desc: String, `interface`: Boolean = false) = +MethodInsnNode(INVOKESTATIC, owner, name, desc, `interface`)
    inline fun invokeinterface(owner: String, name: String, desc: String, `interface`: Boolean = false) = +MethodInsnNode(INVOKEINTERFACE, owner, name, desc, `interface`)
    
    /* Creating new instances */
    
    inline fun new(type: String) = +TypeInsnNode(NEW, type)
    inline fun new(type: KClass<*>) = +TypeInsnNode(NEW, type.internalName)
    inline fun newarray(type: Int) = +IntInsnNode(NEWARRAY, type)
    inline fun anewarray(desc: String) = +TypeInsnNode(ANEWARRAY, desc)
    inline fun newboolarray() = newarray(T_BOOLEAN)
    inline fun newchararray() = newarray(T_CHAR)
    inline fun newbytearray() = newarray(T_BYTE)
    inline fun newshortarray() = newarray(T_SHORT)
    inline fun newintarray() = newarray(T_INT)
    inline fun newlongarray() = newarray(T_LONG)
    inline fun newfloatarray() = newarray(T_FLOAT)
    inline fun newdoublearray() = newarray(T_DOUBLE)
    
    /* Array information */
    
    inline fun arraylength() = insn(ARRAYLENGTH)
    
    /* Throwing exceptions */
    
    inline fun athrow() = insn(ATHROW)
    
    /* Type checks and casts */
    
    inline fun checkcast(descriptor: String) = +TypeInsnNode(CHECKCAST, descriptor)
    inline fun instanceof(descriptor: String) = +TypeInsnNode(INSTANCEOF, descriptor)
    
    /* Returns */
    
    inline fun ireturn() = insn(IRETURN)
    inline fun lreturn() = insn(LRETURN)
    inline fun freturn() = insn(FRETURN)
    inline fun dreturn() = insn(DRETURN)
    inline fun areturn() = insn(ARETURN)
    inline fun _return() = insn(RETURN)
}

fun insnBuilder(builder: InsnBuilder.() -> Unit) = InsnBuilder().also(builder).list