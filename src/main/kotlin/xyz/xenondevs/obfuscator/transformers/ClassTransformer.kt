package xyz.xenondevs.obfuscator.transformers

import org.objectweb.asm.tree.FieldNode
import org.objectweb.asm.tree.MethodNode
import xyz.xenondevs.obfuscator.config.type.TransformerType
import xyz.xenondevs.obfuscator.jvm.ClassWrapper
import xyz.xenondevs.obfuscator.jvm.JavaArchive

abstract class ClassTransformer(name: String, configType: TransformerType, private val methodsFirst: Boolean = false) :
    Transformer(name, configType) {
    
    lateinit var current: ClassWrapper
    
    override fun transformJar(jar: JavaArchive) {
        jar.classes.forEach {
            current = it
            transformClass(it)
            if (methodsFirst) {
                it.methods.forEach(this::transformMethod)
                it.fields.forEach(this::transformField)
            } else {
                it.fields.forEach(this::transformField)
                it.methods.forEach(this::transformMethod)
            }
        }
    }
    
    abstract fun transformClass(clazz: ClassWrapper)
    
    abstract fun transformField(field: FieldNode)
    
    abstract fun transformMethod(method: MethodNode)
    
}