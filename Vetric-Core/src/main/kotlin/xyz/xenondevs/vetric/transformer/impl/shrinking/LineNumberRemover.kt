package xyz.xenondevs.vetric.transformer.impl.shrinking

import org.objectweb.asm.tree.FieldNode
import org.objectweb.asm.tree.LineNumberNode
import org.objectweb.asm.tree.MethodNode
import xyz.xenondevs.bytebase.jvm.ClassWrapper
import xyz.xenondevs.bytebase.jvm.JavaArchive
import xyz.xenondevs.vetric.cli.terminal.debug
import xyz.xenondevs.vetric.cli.terminal.info
import xyz.xenondevs.vetric.transformer.ClassTransformer
import xyz.xenondevs.vetric.transformer.TransformerPriority

class LineNumberRemover: ClassTransformer("LineNumberRemover", TransformerPriority.LOWEST) {
    
    private var totalCounter = 0
    
    override fun transform(archive: JavaArchive) {
        super.transform(archive)
        info("Removed a total of $totalCounter line numbers")
        totalCounter = 0
    }
    
    override fun transformMethod(method: MethodNode) {
        var counter = 0
        method.instructions.removeAll { if (it is LineNumberNode) ++counter; true }
        debug("Removed $counter line numbers from ${method.name}")
        totalCounter += counter
    }
    
    override fun transformClass(clazz: ClassWrapper) = Unit
    
    override fun transformField(field: FieldNode) = Unit
}
