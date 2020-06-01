@file:Suppress("UNCHECKED_CAST")

package xyz.xenondevs.obfuscator.tansformer.string

import org.objectweb.asm.tree.FieldNode
import org.objectweb.asm.tree.MethodNode
import xyz.xenondevs.obfuscator.asm.SmartClass
import xyz.xenondevs.obfuscator.asm.SmartJar
import xyz.xenondevs.obfuscator.asm.dump.CryptDump
import xyz.xenondevs.obfuscator.tansformer.ClassTransformer
import xyz.xenondevs.obfuscator.util.StringUtils.ALPHA
import xyz.xenondevs.obfuscator.util.StringUtils.randomString

@ExperimentalStdlibApi
class EncryptionInjector : ClassTransformer("EncryptionInjector") {

    companion object {
        val methods = HashMap<String, String>()
    }

    override fun transform(jar: SmartJar) {
        val list = ArrayList<SmartClass>()
        val available: ArrayList<SmartClass> = jar.classes.clone() as ArrayList<SmartClass>
        for (i in 1..(if (jar.classes.size < 4) 1 else 4)) {
            val clazz = available.random()
            available.remove(clazz)
            list.add(clazz)
        }
        list.forEach {
            val method = MethodNode()
            val encryptName = randomString(10..40, ALPHA)
            CryptDump.dump(encryptName, method)
            it.addMethod(method)
            println("Injected decryption method into ${it.fileName} as $encryptName")
            it.update()
            methods[it.node.name] = encryptName
            super.transform(jar)
        }
    }

    override fun transform(smartClass: SmartClass) {

    }

    override fun transform(field: FieldNode) {
    }

    override fun transform(method: MethodNode) {
    }

}