package xyz.xenondevs.vetric.transformer

import org.objectweb.asm.tree.FieldNode
import org.objectweb.asm.tree.MethodNode
import xyz.xenondevs.bytebase.jvm.ClassWrapper
import xyz.xenondevs.bytebase.jvm.JavaArchive

abstract class ClassTransformer(
    name: String,
    priority: TransformerPriority
) : Transformer(name, priority) {
    
    protected lateinit var currentJar: JavaArchive
    protected lateinit var currentClass: ClassWrapper
    private var skipMembers = false
    
    override fun transform(archive: JavaArchive) {
        currentJar = archive
        archive.classes.forEach { clazz ->
            currentClass = clazz
            transformClass(clazz)
            if (skipMembers) {
                skipMembers = false
                return@forEach
            }
            clazz.fields.forEach(::transformField)
            clazz.methods.forEach(::transformMethod)
        }
    }
    
    abstract fun transformClass(clazz: ClassWrapper)
    
    abstract fun transformField(field: FieldNode)
    
    abstract fun transformMethod(method: MethodNode)
    
    fun skipClass() {
        skipMembers = true
    }
    
}