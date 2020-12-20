package xyz.xenondevs.vetric.transformers.renamer

import com.google.gson.JsonObject
import org.objectweb.asm.Handle
import org.objectweb.asm.Opcodes.ACC_PUBLIC
import org.objectweb.asm.Opcodes.ACC_STATIC
import org.objectweb.asm.tree.FieldInsnNode
import org.objectweb.asm.tree.InvokeDynamicInsnNode
import org.objectweb.asm.tree.MethodInsnNode
import org.objectweb.asm.tree.MethodNode
import xyz.xenondevs.vetric.config.type.TransformerConfig
import xyz.xenondevs.vetric.jvm.ClassWrapper
import xyz.xenondevs.vetric.jvm.JavaArchive
import xyz.xenondevs.vetric.transformers.Transformer
import xyz.xenondevs.vetric.transformers.TransformerPriority.HIGHEST
import xyz.xenondevs.vetric.utils.access
import xyz.xenondevs.vetric.utils.accessWrapper
import xyz.xenondevs.vetric.utils.json.getBoolean
import xyz.xenondevs.vetric.utils.ownerWrapper

// TODO When exclusion is added add, check to isMoveable
object Shuffler : Transformer("Shuffler", ShufflerConfig, HIGHEST) {
    
    var shuffleFields = true
    var shuffleMethods = true
    var crossClassMethods = false
    var crossClassFields = false
    
    val mappings = HashMap<String, String>()
    val processedMethods = ArrayList<MethodNode>()
    
    override fun transformJar(jar: JavaArchive) {
        mappings.clear()
        if (crossClassMethods)
            shuffleMethodsCC(jar)
        jar.classes.forEach {
            if (shuffleFields && !it.fields.isNullOrEmpty())
                it.fields.shuffle()
            if (shuffleMethods && !it.methods.isNullOrEmpty())
                it.methods.shuffle()
        }
        processedMethods.clear()
    }
    
    private fun shuffleMethodsCC(jar: JavaArchive) {
        val available = jar.classes.filter { it.accessWrapper.isPublicClass() }
        jar.classes.forEach { clazz ->
            clazz.methods.filter { this.isMoveable(it, clazz) }.forEach method@{ method ->
                val newClass = available.filter { method !in it }.randomOrNull() ?: return@method
                method.access = ACC_PUBLIC or ACC_STATIC
                newClass.methods.add(method)
                clazz.methods.remove(method)
                processedMethods.add(method)
                mappings["${clazz.name}.${method.name}${method.desc}"] = newClass.name
            }
        }
        replaceMethodReferences(jar)
    }
    
    private fun isMoveable(method: MethodNode, clazz: ClassWrapper): Boolean {
        return method.accessWrapper.isStatic()
            && Renamer.isRenameable(method, clazz)
            && !processedMethods.contains(method)
            && method.instructions.filterIsInstance<FieldInsnNode>().all { it.access.isPublic() && (!it.access.isStatic() || it.ownerWrapper.accessWrapper.isPublic()) }
            && method.instructions.filterIsInstance<MethodInsnNode>().all { it.access.isPublic() && (!it.access.isStatic() || it.ownerWrapper.accessWrapper.isPublic()) }
    }
    
    private fun replaceMethodReferences(jar: JavaArchive) {
        jar.forEachInstruction { insn ->
            if (insn is MethodInsnNode && "${insn.owner}.${insn.name}${insn.desc}" in mappings)
                insn.owner = mappings["${insn.owner}.${insn.name}${insn.desc}"]
            else if (insn is InvokeDynamicInsnNode) {
                insn.bsmArgs?.forEachIndexed { index, obj ->
                    if (obj is Handle && "${obj.owner}.${obj.name}${obj.desc}" in mappings)
                        insn.bsmArgs[index] = Handle(obj.tag, mappings["${obj.owner}.${obj.name}${obj.desc}"], obj.name, obj.desc, obj.isInterface)
                }
            }
        }
    }
    
    object ShufflerConfig : TransformerConfig(Shuffler::class) {
        override fun parse(obj: JsonObject) {
            super.parse(obj)
            shuffleFields = obj.getBoolean("fields", true)
            shuffleMethods = obj.getBoolean("methods", true)
            crossClassMethods = obj.getBoolean("crossclassmethods", false)
            crossClassFields = obj.getBoolean("crossclassfields", false)
        }
    }
    
}