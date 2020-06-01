package xyz.xenondevs.obfuscator.tansformer.renamer

import org.objectweb.asm.Type
import org.objectweb.asm.Type.OBJECT
import org.objectweb.asm.tree.*
import xyz.xenondevs.obfuscator.asm.SmartClass
import xyz.xenondevs.obfuscator.asm.SmartJar
import xyz.xenondevs.obfuscator.tansformer.ClassTransformer
import xyz.xenondevs.obfuscator.util.AsmUtils
import xyz.xenondevs.obfuscator.util.StringUtils
import xyz.xenondevs.obfuscator.util.StringUtils.ALPHA

@ExperimentalStdlibApi
class ClassRenamer : ClassTransformer("ClassRenamer") {

    var nameMap = HashMap<String, String>()

    override fun transform(jar: SmartJar) {
        jar.classes.forEach(this::provideNewName)
        super.transform(jar)
        nameMap.forEach { (old, new) -> println("Renamed $old to $new") }
    }

    fun provideNewName(smartClass: SmartClass) {
        val newName = StringUtils.randomString(10..30, ALPHA)
        nameMap[smartClass.node.name] = newName
        smartClass.fileName = "$newName.class"
        smartClass.node.name = newName
    }

    override fun transform(smartClass: SmartClass) {
        if (nameMap.containsKey(smartClass.node.superName))
            smartClass.node.superName = nameMap[smartClass.node.superName]
        if (smartClass.node.interfaces != null) {
            val toAdd = ArrayList<String>()
            nameMap.forEach { (old, new) ->
                run {
                    if (smartClass.node.interfaces.contains(old)) {
                        toAdd.add(new)
                        smartClass.node.interfaces.remove(old)
                    }
                }
            }
            smartClass.node.interfaces.addAll(toAdd)
        }
    }

    override fun transform(field: FieldNode) {
        val name = Type.getType(field.desc).internalName
        field.desc = "L${nameMap.getOrDefault(name, name)};"
    }

    override fun transform(method: MethodNode) {
        method.desc = getMethodDescriptor(method.desc)
        method.instructions.forEach {
            run {
                if (it is FieldInsnNode) {
                    val typeName = Type.getType(it.desc).internalName
                    if (nameMap.containsKey(typeName))
                        it.desc = nameMap[typeName]
                    it.owner = nameMap.getOrDefault(it.owner, it.owner)
                } else if (it is MethodInsnNode) {
                    it.desc = getMethodDescriptor(it.desc)
                    it.owner = nameMap.getOrDefault(it.owner, it.owner)
                } else if (it is TypeInsnNode) {
                    if (nameMap.containsKey(it.desc))
                        it.desc = nameMap[it.desc]
                }
            }
        }
    }

    fun getMethodDescriptor(desc: String): String {
        val parameters = Type.getArgumentTypes(desc)
        var returnType = Type.getReturnType(desc)
        val newParams = parameters.map {
            when (it.sort) {
                OBJECT -> Type.getType("L${nameMap.getOrDefault(it.internalName, it.internalName)};")
                else -> it
            }
        }
        if (nameMap.containsKey(returnType.internalName))
            returnType = Type.getType("L${nameMap[returnType.internalName]};")
        return AsmUtils.buildMethodDesc(returnType, newParams)
    }

}