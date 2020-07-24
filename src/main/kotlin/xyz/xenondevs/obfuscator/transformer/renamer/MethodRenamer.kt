package xyz.xenondevs.obfuscator.transformer.renamer

import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.FieldNode
import org.objectweb.asm.tree.MethodInsnNode
import org.objectweb.asm.tree.MethodNode
import xyz.xenondevs.obfuscator.asm.SmartClass
import xyz.xenondevs.obfuscator.asm.SmartJar
import xyz.xenondevs.obfuscator.transformer.ClassTransformer
import xyz.xenondevs.obfuscator.util.AsmUtils
import xyz.xenondevs.obfuscator.util.StringUtils
import java.io.IOException

@ExperimentalStdlibApi
class MethodRenamer : ClassTransformer("Methodrenamer") {

    val nameMap: HashMap<String, HashMap<String, HashMap<String, String>>> = HashMap()
    val runAgain = ArrayList<Pair<MethodNode, ClassNode>>()

    override fun transform(jar: SmartJar) {
        jar.classes.forEach { smartClass ->
            smartClass.methods?.forEach {
                if (!provideName(it, smartClass.node))
                    runAgain.add(it to smartClass.node)
            }
        }
        runAgain.forEach { it.first.name = getName(it.first, it.second) }
        runAgain.clear()
        super.transform(jar)
    }

    override fun transform(smartClass: SmartClass) = Unit

    private fun getName(method: MethodNode, clazz: ClassNode): String {
        var parentClass = AsmUtils.getParent(method, clazz)
        if (parentClass == null)
            parentClass = clazz
        if (nameMap.containsKey(parentClass.name)
                && nameMap[parentClass.name]!!.containsKey(method.name)
                && nameMap[parentClass.name]!![method.name]!!.containsKey(method.desc))
            return nameMap[parentClass.name]!![method.name]!![method.desc]!!
        return method.name
    }

    private fun provideName(method: MethodNode, node: ClassNode): Boolean {
        if (method.name == "<init>" || method.name == "<clinit>"
                || (method.name == "main" && method.desc == "([Ljava/lang/String;)V"))
            return true
        if (AsmUtils.isInherited(method, node))
            return false

        val newName = StringUtils.randomStringUnique()
        if (!nameMap.containsKey(node.name))
            nameMap[node.name] = HashMap()
        if (!nameMap[node.name]!!.containsKey(method.name))
            nameMap[node.name]!![method.name] = HashMap()
        nameMap[node.name]!![method.name]!![method.desc] = newName
        println("${node.name}.${method.name + method.desc} -> $newName")
        method.name = newName
        return true
    }

    override fun transform(field: FieldNode) = Unit

    override fun transform(method: MethodNode) {
        method.instructions.filterIsInstance<MethodInsnNode>().forEach { insn ->
            run {
                try {
                    val node = AsmUtils.asClassNode(insn.owner)
                    insn.name = getName(MethodNode().also { it.name = insn.name; it.desc = insn.desc }, node)
                } catch (ex: IOException) {
                    return@run
                }
            }
        }
    }
}