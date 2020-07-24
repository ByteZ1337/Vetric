package xyz.xenondevs.obfuscator.transformer.renamer

import org.objectweb.asm.tree.FieldInsnNode
import org.objectweb.asm.tree.FieldNode
import org.objectweb.asm.tree.MethodNode
import xyz.xenondevs.obfuscator.asm.SmartClass
import xyz.xenondevs.obfuscator.transformer.ClassTransformer
import xyz.xenondevs.obfuscator.util.AsmUtils
import xyz.xenondevs.obfuscator.util.StringUtils

@ExperimentalStdlibApi
class FieldRenamer : ClassTransformer("Field Renamer", true) {

    var nameMap = HashMap<String, HashMap<String, String>>()
    // TODO different descriptors

    override fun transform(field: FieldNode) {
        if (AsmUtils.isEnum(currentClass.node.access) && field.name == "\$VALUES")
            return
        val newName = StringUtils.randomString(10..20)
        val map = nameMap.getOrDefault(currentClass.node.name, HashMap())
        map[field.name] = newName
        println("${field.name} -> $newName")
        field.name = newName
        nameMap[currentClass.node.name] = map
    }

    override fun transform(method: MethodNode) {
        method.instructions.forEach {
            run {
                if (it is FieldInsnNode && nameMap.containsKey(it.owner) && nameMap[it.owner]?.containsKey(it.name)!!)
                    it.name = nameMap[it.owner]!![it.name]
            }
        }
    }

    override fun transform(smartClass: SmartClass) = Unit
}