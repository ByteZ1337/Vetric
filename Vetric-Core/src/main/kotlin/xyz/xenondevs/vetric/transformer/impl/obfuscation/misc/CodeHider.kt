package xyz.xenondevs.vetric.transformer.impl.obfuscation.misc

import org.objectweb.asm.tree.FieldNode
import org.objectweb.asm.tree.MethodNode
import xyz.xenondevs.bytebase.jvm.ClassWrapper
import xyz.xenondevs.bytebase.util.accessWrapper
import xyz.xenondevs.bytebase.util.hasAnnotations
import xyz.xenondevs.vetric.config.JsonConfig
import xyz.xenondevs.vetric.transformer.ClassTransformer
import xyz.xenondevs.vetric.transformer.TransformerPriority

object CodeHider : ClassTransformer("CodeHider", TransformerPriority.LOWEST) {
    
    private var hideClasses = true
    private var hideFields = true
    private var hideMethods = true
    
    override fun transformClass(clazz: ClassWrapper) {
        if (clazz.hasAnnotations())
            skipClass()
        
        if (hideClasses && !clazz.isInterface())
            clazz.accessWrapper.setSynthetic(true)
    }
    
    override fun transformField(field: FieldNode) {
        if (hideFields && !field.hasAnnotations())
            field.accessWrapper.setSynthetic(true)
    }
    
    override fun transformMethod(method: MethodNode) {
        if (hideMethods && !method.hasAnnotations())
            method.accessWrapper.setSynthetic(true)
    }
    
    override fun loadConfig(config: JsonConfig) {
        hideClasses = config.getBoolean("classes", true)
        hideFields = config.getBoolean("fields", true)
        hideMethods = config.getBoolean("methods", true)
    }
    
}