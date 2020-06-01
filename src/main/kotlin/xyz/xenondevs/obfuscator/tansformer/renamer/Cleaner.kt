package xyz.xenondevs.obfuscator.tansformer.renamer

import org.objectweb.asm.tree.FieldNode
import org.objectweb.asm.tree.LineNumberNode
import org.objectweb.asm.tree.MethodNode
import xyz.xenondevs.obfuscator.asm.SmartClass
import xyz.xenondevs.obfuscator.tansformer.ClassTransformer

@ExperimentalStdlibApi
class Cleaner : ClassTransformer("Cleaner") {

    override fun transform(smartClass: SmartClass) {
        smartClass.node.sourceDebug = null
        smartClass.node.sourceFile = null
    }

    override fun transform(field: FieldNode) {
        field.signature = null
    }

    override fun transform(method: MethodNode) {
        method.signature = null
        method.localVariables?.clear()
        method.instructions.filterIsInstance<LineNumberNode>().forEach(method.instructions::remove)
    }
}