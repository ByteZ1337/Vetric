package xyz.xenondevs.vetric.transformer.obfuscation.misc

import com.google.gson.JsonObject
import org.objectweb.asm.tree.FieldNode
import org.objectweb.asm.tree.MethodNode
import xyz.xenondevs.vetric.config.type.TransformerConfig
import xyz.xenondevs.vetric.jvm.ClassWrapper
import xyz.xenondevs.vetric.transformer.ClassTransformer
import xyz.xenondevs.vetric.util.asm.accessWrapper
import xyz.xenondevs.vetric.util.asm.hasAnnotations
import xyz.xenondevs.vetric.util.json.getBoolean

object CodeHider : ClassTransformer("CodeHider", CodeHiderConfig) {
    
    var classes = true
    var fields = true
    var methods = true
    
    override fun transformClass(clazz: ClassWrapper) {
        if (clazz.isInterface())
            skipClass()
        else if (classes && !clazz.hasAnnotations())
            clazz.accessWrapper.setSynthetic()
    }
    
    override fun transformField(field: FieldNode) {
        if (fields && !field.hasAnnotations())
            field.accessWrapper.setSynthetic()
    }
    
    override fun transformMethod(method: MethodNode) {
        if (methods && !method.hasAnnotations()) {
            method.accessWrapper.setSynthetic()
        }
    }
    
    private object CodeHiderConfig : TransformerConfig(CodeHider::class) {
        
        override fun parse(obj: JsonObject) {
            super.parse(obj)
            if (enabled) {
                classes = obj.getBoolean("classes", true)
                fields = obj.getBoolean("fields", true)
                methods = obj.getBoolean("methods", true)
            }
        }
    }
    
}