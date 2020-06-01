package xyz.xenondevs.obfuscator.tansformer.renamer

import org.objectweb.asm.tree.FieldNode
import org.objectweb.asm.tree.MethodNode
import xyz.xenondevs.obfuscator.asm.SmartClass
import xyz.xenondevs.obfuscator.tansformer.ClassTransformer
import xyz.xenondevs.obfuscator.util.StringUtils.ALPHA
import xyz.xenondevs.obfuscator.util.StringUtils.randomString

@ExperimentalStdlibApi
class LocalRenamer : ClassTransformer("Local Renamer") {

    override fun transform(smartClass: SmartClass) {
    }

    override fun transform(field: FieldNode) {
    }

    override fun transform(method: MethodNode) {
        method.localVariables.forEach { it.name = randomString(20..50, ALPHA) }
    }
}