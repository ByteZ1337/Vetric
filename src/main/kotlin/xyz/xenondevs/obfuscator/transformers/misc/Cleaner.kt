package xyz.xenondevs.obfuscator.transformers.misc

import org.objectweb.asm.tree.FieldNode
import org.objectweb.asm.tree.LineNumberNode
import org.objectweb.asm.tree.MethodNode
import xyz.xenondevs.obfuscator.config.type.TransformerType
import xyz.xenondevs.obfuscator.jvm.ClassWrapper
import xyz.xenondevs.obfuscator.jvm.JavaArchive
import xyz.xenondevs.obfuscator.transformers.ClassTransformer

object Cleaner : ClassTransformer("Cleaner", TransformerType(Cleaner::class)) {
    
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