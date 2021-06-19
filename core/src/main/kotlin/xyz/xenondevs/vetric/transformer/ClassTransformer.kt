package xyz.xenondevs.vetric.transformer

import org.objectweb.asm.tree.FieldNode
import org.objectweb.asm.tree.MethodNode
import xyz.xenondevs.vetric.config.type.TransformerConfig
import xyz.xenondevs.vetric.exclusion.ExclusionManager
import xyz.xenondevs.vetric.jvm.ClassWrapper
import xyz.xenondevs.vetric.jvm.JavaArchive

abstract class ClassTransformer(
    name: String,
    config: TransformerConfig,
    priority: TransformerPriority = TransformerPriority.NORMAL
) : Transformer(name, config, priority) {
    
    lateinit var currentJar: JavaArchive
    lateinit var currentClass: ClassWrapper
    private var skipMembers = false
    
    override fun transformJar(jar: JavaArchive) {
        currentJar = jar
        jar.classes.filterNot(ExclusionManager::isExcluded).forEach { clazz ->
            currentClass = clazz
            transformClass(clazz)
            if (skipMembers) {
                skipMembers = false
                return@forEach
            }
            clazz.fields.filterNot { ExclusionManager.isExcluded(clazz, it) }.forEach(this::transformField)
            clazz.methods.filterNot { ExclusionManager.isExcluded(clazz, it) }.forEach(this::transformMethod)
        }
    }
    
    abstract fun transformClass(clazz: ClassWrapper)
    
    abstract fun transformField(field: FieldNode)
    
    abstract fun transformMethod(method: MethodNode)
    
    fun skipClass() {
        skipMembers = true
    }
    
}