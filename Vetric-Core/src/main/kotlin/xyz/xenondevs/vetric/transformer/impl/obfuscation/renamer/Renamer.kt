package xyz.xenondevs.vetric.transformer.impl.obfuscation.renamer

import org.objectweb.asm.tree.FieldNode
import org.objectweb.asm.tree.MethodNode
import xyz.xenondevs.bytebase.asm.refactor.Refactorer
import xyz.xenondevs.bytebase.jvm.ClassWrapper
import xyz.xenondevs.bytebase.jvm.JavaArchive
import xyz.xenondevs.bytebase.jvm.VirtualClassPath
import xyz.xenondevs.bytebase.util.accessWrapper
import xyz.xenondevs.vetric.config.JsonConfig
import xyz.xenondevs.vetric.supplier.impl.AlphaSupplier
import xyz.xenondevs.vetric.transformer.Transformer
import xyz.xenondevs.vetric.transformer.TransformerPriority
import java.io.File

object Renamer : Transformer("Renamer", TransformerPriority.LOW) {
    
    /**
     * if this is set to true, fields/method with different descriptors will be renamed to the same name
     */
    var repeatNames = true
    
    var renamePackages = true
    var renameClasses = true
    var renameMethods = true
    var renameFields = true
    var renameLocals = true
    
    var packageSupplier = AlphaSupplier()
    var classSupplier = AlphaSupplier()
    var methodSupplier = AlphaSupplier()
    var fieldSupplier = AlphaSupplier()
    var localSupplier = AlphaSupplier()
    
    var mappings = HashMap<String, String>()
    
    override fun transform(archive: JavaArchive) {
        VirtualClassPath.loadJar(archive)
        generateMappings(archive)
        applyMappings(archive)
        VirtualClassPath.classes.clear()
        VirtualClassPath.inheritanceTrees.clear()
        VirtualClassPath.loadJar(archive)
    }
    
    private fun generateMappings(archive: JavaArchive) {
        mappings = MappingsGenerator(archive).generateMappings()
    }
    
    private fun applyMappings(archive: JavaArchive) {
        val refactorer = Refactorer(archive, mappings)
        refactorer.refactor()
    }
    
    override fun loadConfig(config: JsonConfig) {
        super.loadConfig(config)
    }
    
}

/**
 * Checks if a field is renamable. A field can't be renamed if it starts with a ``$``.
 */
fun FieldNode.isRenamable() = !name.startsWith('$')

/**
 * Checks if a method is renamable.
 */
fun MethodNode.isRenamable(owner: ClassWrapper, /*mappings: HashMap<String, String>*/) =
    !accessWrapper.isNative()
        && !name.startsWith('<')
        && "main" != name && "premain" != name
        && !(owner.isEnum() && accessWrapper.isStatic() && ("values" == name || "valueOf" == name))
        //&& "${owner.name}.$name$desc" in mappings
        && owner.superClasses.none { it.getMethod(name, desc) != null }