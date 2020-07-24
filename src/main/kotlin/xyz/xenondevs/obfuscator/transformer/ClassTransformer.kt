@file:Suppress("UNUSED_PARAMETER")

package xyz.xenondevs.obfuscator.transformer

import org.objectweb.asm.tree.FieldNode
import org.objectweb.asm.tree.MethodNode
import xyz.xenondevs.obfuscator.asm.SmartClass
import xyz.xenondevs.obfuscator.asm.SmartJar

@ExperimentalStdlibApi
abstract class ClassTransformer(name: String, private val fieldsFirst: Boolean = false) : Transformer(name) {

    lateinit var currentClass: SmartClass

    override fun transform(jar: SmartJar) {
        jar.classes.forEach {
            currentClass = it
            transform(it)
            if (fieldsFirst) {
                it.fields?.forEach(this::transform)
                it.methods?.forEach(this::transform)
            } else {
                it.methods?.forEach(this::transform)
                it.fields?.forEach(this::transform)
            }
            jar.files.remove(it.originalName)
            jar.files.remove(it.fileName)
            jar.files[it.fileName] = it.toByteCode()
        }
    }

    abstract fun transform(smartClass: SmartClass)

    abstract fun transform(field: FieldNode)

    abstract fun transform(method: MethodNode)
}