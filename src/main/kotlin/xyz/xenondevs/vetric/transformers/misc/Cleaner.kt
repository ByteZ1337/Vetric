package xyz.xenondevs.vetric.transformers.misc

import org.objectweb.asm.tree.FieldNode
import org.objectweb.asm.tree.LineNumberNode
import org.objectweb.asm.tree.MethodNode
import xyz.xenondevs.vetric.config.type.TransformerConfig
import xyz.xenondevs.vetric.jvm.ClassWrapper
import xyz.xenondevs.vetric.jvm.JavaArchive
import xyz.xenondevs.vetric.transformers.ClassTransformer

object Cleaner : ClassTransformer("Cleaner", TransformerConfig(Cleaner::class)) {
    
    override fun transformJar(jar: JavaArchive) {
        super.transformJar(jar)
        println("Removed useless instructions and debug info.")
    }
    
    override fun transformClass(clazz: ClassWrapper) {
        clazz.sourceDebug = null
        clazz.sourceFile = null
        clazz.signature = null
        clazz.outerClass = null
        clazz.outerMethod = null
        clazz.outerMethodDesc = null
        clazz.innerClasses = emptyList()
    }
    
    override fun transformField(field: FieldNode) {
        field.signature = null
    }
    
    override fun transformMethod(method: MethodNode) {
        method.signature = null
        method.localVariables = null // Local variable obfuscation is bypassed easily and is just a waste of space
        method.instructions.filterIsInstance<LineNumberNode>().forEach(method.instructions::remove)
    }
    
}