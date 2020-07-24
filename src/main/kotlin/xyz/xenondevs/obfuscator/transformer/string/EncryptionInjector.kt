@file:Suppress("UNCHECKED_CAST")

package xyz.xenondevs.obfuscator.transformer.string

import org.objectweb.asm.tree.FieldNode
import org.objectweb.asm.tree.MethodNode
import xyz.xenondevs.obfuscator.asm.SmartClass
import xyz.xenondevs.obfuscator.asm.SmartJar
import xyz.xenondevs.obfuscator.asm.dump.CryptDump
import xyz.xenondevs.obfuscator.transformer.ClassTransformer
import xyz.xenondevs.obfuscator.util.AsmUtils
import xyz.xenondevs.obfuscator.util.StringUtils
import xyz.xenondevs.obfuscator.util.StringUtils.randomString

@ExperimentalStdlibApi
class EncryptionInjector : ClassTransformer("EncryptionInjector") {

    companion object {
        val methods = HashMap<String, String>()
    }


    override fun transform(jar: SmartJar) {
        val list = ArrayList<SmartClass>()
        val available = jar.classes.filter { !AsmUtils.isInterface(it.node.access) } as ArrayList<SmartClass>
        for (i in 1..(if (jar.classes.count { !AsmUtils.isInterface(it.node.access) } < 4) 1 else 4)) {
            val clazz = available.random()
            available.remove(clazz)
            list.add(clazz)
        }
        list.forEach {
            val method = MethodNode()
            val encryptName = "decrypt" + randomString(2, StringUtils.NUMERIC)
            CryptDump.dump(encryptName, method)
            it.addMethod(method)
            println("Injected decryption method into ${it.fileName} as $encryptName")
            it.update()
            methods[it.node.name] = encryptName
            super.transform(jar)
        }
    }

    override fun transform(smartClass: SmartClass) = Unit

    override fun transform(field: FieldNode) = Unit

    override fun transform(method: MethodNode) = Unit

}