package xyz.xenondevs.vetric.util.asm

import org.objectweb.asm.Opcodes.*
import org.objectweb.asm.Type
import org.objectweb.asm.tree.*
import xyz.xenondevs.vetric.jvm.ClassPath
import xyz.xenondevs.vetric.jvm.ClassWrapper
import xyz.xenondevs.vetric.jvm.JavaArchive
import xyz.xenondevs.vetric.util.accessWrapper
import xyz.xenondevs.vetric.util.startsWithAny

object ASMUtils {
    
    /* ------------------ Constants ------------------ */
    
    const val OBJECT_TYPE = "java/lang/Object"
    
    /* ------------------ General Utilities ------------------ */
    
    fun getSuperClasses(wrapper: ClassWrapper, current: Set<String> = emptySet()): Set<String> {
        val parents = HashSet<String>()
        
        val superName = wrapper.superName
        if (!current.contains(superName)) {
            parents += superName
            if (OBJECT_TYPE != superName)
                parents += getSuperClasses(ClassPath.getClassWrapper(superName), parents)
        }
        
        wrapper.interfaces?.forEach {
            if (!current.contains(it)) {
                parents += it
                parents += getSuperClasses(ClassPath.getClassWrapper(it), parents)
            }
        }
        
        if (!current.contains(wrapper.name))
            parents += wrapper.name
        
        return parents
    }
    
    // TODO move to ClassWrapper
    fun isAssignableFrom(type1: String, type2: String): Boolean {
        if (OBJECT_TYPE == type1 || type1 == type2)
            return true
        
        val wrapper = ClassPath.getClassWrapper(type2)
        return getSuperClasses(wrapper).contains(type1)
    }
    
    // TODO Implement getSuperClasses
    fun getParent(method: MethodNode, clazz: ClassNode): ClassWrapper? {
        if (method.accessWrapper.isStatic())
            return null
        
        if (clazz.superName != null) {
            val superClass = ClassPath.getClassWrapper(clazz.superName)
            val rec = getParent(method, superClass)
            if (hasMethod(method.name, method.desc, superClass) && rec == null)
                return superClass
            if (rec != null)
                return rec
        }
        
        if (clazz.interfaces != null) {
            clazz.interfaces.forEach { i ->
                val interfaceClass = ClassPath.getClassWrapper(i)
                val rec = getParent(method, interfaceClass)
                if (hasMethod(method.name, method.desc, interfaceClass) && rec == null)
                    return interfaceClass
                if (rec != null)
                    return rec
            }
        }
        return null
    }
    
    fun AnnotationNode.toHashMap(): HashMap<String, Any?> {
        val map = HashMap<String, Any?>()
        values.filterIndexed { index, _ -> index % 2 == 0 }.forEachIndexed { index, any ->
            map[any.toString()] = values[index * 2 + 1]
        }
        return map
    }
    
    fun isInherited(method: MethodNode, clazz: ClassNode) =
        getParent(method, clazz) != null
    
    fun hasMethod(name: String, desc: String, clazz: ClassNode) =
        clazz.methods != null && clazz.methods.any { it.desc == desc && it.name == name }
    
    fun getIntInsn(value: Int) =
        when (value) {
            in -1..5 -> InsnNode(value + 3)
            in Byte.MIN_VALUE..Byte.MAX_VALUE -> IntInsnNode(BIPUSH, value)
            in Short.MIN_VALUE..Short.MAX_VALUE -> IntInsnNode(SIPUSH, value)
            else -> LdcInsnNode(value)
        }
    
    fun getInt(insn: AbstractInsnNode) =
        when {
            insn.opcode in ICONST_M1..ICONST_5 -> insn.opcode - 3
            insn is IntInsnNode && (insn.opcode == BIPUSH || insn.opcode == SIPUSH) -> insn.operand
            insn is LdcInsnNode && insn.cst is Int -> insn.cst as Int
            else -> error("The given instruction is not an integer")
        }
    
    fun getLongInsn(value: Long) =
        when (value) {
            in 0..1 -> InsnNode((value + 9).toInt())
            else -> LdcInsnNode(value)
        }
    
    fun getLong(insn: AbstractInsnNode) =
        when {
            insn.opcode in LCONST_0..LCONST_1 -> (insn.opcode - 9).toLong()
            insn is LdcInsnNode && insn.cst is Long -> insn.cst as Long
            else -> error("The given instruction is not a long")
        }
    
    fun getFloatInsn(value: Float) =
        when {
            value % 1 == 0f && value in 0f..2f -> InsnNode((value + 11).toInt())
            else -> LdcInsnNode(value)
        }
    
    fun getFloat(insn: AbstractInsnNode) =
        when {
            insn.opcode in FCONST_0..FCONST_2 -> (insn.opcode - 11).toFloat()
            insn is LdcInsnNode && insn.cst is Float -> insn.cst as Float
            else -> error("The given instruction is not a float")
        }
    
    fun getDoubleInsn(value: Double) =
        when {
            value % 1 == 0.0 && value in 0.0..1.0 -> InsnNode((value + 14).toInt())
            else -> LdcInsnNode(value)
        }
    
    fun getDouble(insn: AbstractInsnNode) =
        when {
            insn.opcode in DCONST_0..DCONST_1 -> (insn.opcode - 14).toDouble()
            insn is LdcInsnNode && insn.cst is Double -> insn.cst as Double
            else -> error("The given instruction is not a double")
        }
    
    fun getType(name: String): Type {
        if (name.length > 1 && !name.startsWithAny('L', '[', '('))
            return Type.getType("L$name;")
        return Type.getType(name)
    }
    
    class InsnParent(val jar: JavaArchive, val clazz: ClassWrapper, val method: MethodNode, val insnList: InsnList)
    
    class MemberReference(val owner: String, val name: String, val desc: String) {
        
        fun transform(insn: FieldInsnNode) {
            insn.owner = owner
            insn.name = name
            insn.desc = desc
        }
        
        fun transform(insn: MethodInsnNode) {
            insn.owner = owner
            insn.name = name
            insn.desc = desc
        }
        
        override fun toString() =
            if (desc.contains('(')) "$owner.$name$desc" else "$owner.$name.$desc"
        
    }
}
