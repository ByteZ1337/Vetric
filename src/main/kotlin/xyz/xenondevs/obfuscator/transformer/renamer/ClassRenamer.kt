package xyz.xenondevs.obfuscator.transformer.renamer

import org.objectweb.asm.Type
import org.objectweb.asm.Type.*
import org.objectweb.asm.tree.*
import xyz.xenondevs.obfuscator.asm.SmartClass
import xyz.xenondevs.obfuscator.asm.SmartJar
import xyz.xenondevs.obfuscator.transformer.ClassTransformer
import xyz.xenondevs.obfuscator.util.AsmUtils
import xyz.xenondevs.obfuscator.util.StringUtils
import xyz.xenondevs.obfuscator.util.between

@ExperimentalStdlibApi
class ClassRenamer : ClassTransformer("Classrenamer") {

    var nameMap = HashMap<String, String>()

    override fun transform(jar: SmartJar) {
        jar.classes.forEach(this::provideName)
        super.transform(jar)
    }

    override fun transform(smartClass: SmartClass) {
        smartClass.node.superName = processDescriptor(smartClass.node.superName)
        processIntefaces(smartClass)
    }

    override fun transform(field: FieldNode) {
        field.desc = processDescriptor(field.desc)
    }

    override fun transform(method: MethodNode) {
        method.desc = processMethodDescriptor(method.desc)
        processInstructions(method.instructions)
    }

    private fun provideName(smartClass: SmartClass) {
        val newName = StringUtils.randomStringUnique()
        println("${smartClass.node.name} -> $newName")
        nameMap[smartClass.node.name] = newName
        smartClass.fileName = "$newName.class"
        smartClass.node.name = newName
    }

    private fun processDescriptor(desc: String): String {
        if (desc.first() != '[' && desc.last() != ';')
            return nameMap.getOrDefault(desc, desc)
        val type = getType(desc)
        if (type.sort == OBJECT) {
            if (nameMap.containsKey(type.internalName))
                return "L${nameMap[type.internalName]};"
        } else if (type.sort == ARRAY)
            return processArrayDesc(type)
        return desc
    }

    private fun processArrayDesc(type: Type): String {
        type.internalName.between("L", ";")
        val name = type.internalName.substring(type.internalName.lastIndexOf('[') + 2, type.internalName.length - 1)
        return if (nameMap.containsKey(name))
            "${type.internalName.substringBefore('L')}L${nameMap[name]};"
        else type.descriptor
    }

    private fun processIntefaces(smartClass: SmartClass) {
        val node = smartClass.node
        if (node.interfaces != null)
            node.interfaces.forEach {
                if (nameMap.containsKey(it)) {
                    node.interfaces.remove(it)
                    node.interfaces.add(nameMap[it])
                }
            }
    }

    private fun processMethodDescriptor(desc: String): String {
        val type = getType(desc)
        if (type.sort == METHOD) {
            val returnType = getType(processDescriptor(type.returnType.descriptor))
            val params = type.argumentTypes.map { getType(processDescriptor(it.descriptor)) }
            return AsmUtils.buildMethodDesc(returnType, params)
        }
        return desc
    }

    private fun processInstructions(instructions: InsnList) {
        instructions.forEach {
            if (it is TypeInsnNode)
                it.desc = processDescriptor(it.desc)
            else if (it is FieldInsnNode) {
                it.owner = processDescriptor(it.owner)
                it.desc = processDescriptor(it.desc)
            } else if (it is MethodInsnNode) {
                it.owner = processDescriptor(it.owner)
                it.desc = processMethodDescriptor(it.desc)
            } else if (it is LdcInsnNode && it.cst is Type)
                it.cst = getType(processDescriptor((it.cst as Type).descriptor))
            else if (it is FrameNode && it.local != null) {
                val list = ArrayList<Any?>()
                it.local.forEach { obj -> list += if (obj is String) processDescriptor(obj) else obj }
                it.local = list
            }
        }
    }
}