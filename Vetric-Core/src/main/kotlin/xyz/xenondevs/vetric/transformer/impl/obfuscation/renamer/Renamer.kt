package xyz.xenondevs.vetric.transformer.impl.obfuscation.renamer

import org.objectweb.asm.tree.FieldNode
import org.objectweb.asm.tree.MethodNode
import xyz.xenondevs.bytebase.asm.refactor.Refactorer
import xyz.xenondevs.bytebase.jvm.ClassWrapper
import xyz.xenondevs.bytebase.jvm.JavaArchive
import xyz.xenondevs.bytebase.jvm.VirtualClassPath
import xyz.xenondevs.bytebase.util.accessWrapper
import xyz.xenondevs.vetric.config.JsonConfig
import xyz.xenondevs.vetric.logging.warn
import xyz.xenondevs.vetric.supplier.DEFAULT_SUPPLIER
import xyz.xenondevs.vetric.transformer.Transformer
import xyz.xenondevs.vetric.transformer.TransformerPriority
import java.io.File

object Renamer : Transformer("Renamer", TransformerPriority.LOW) {
    
    /**
     * if this is set to true, fields/method with different descriptors will be renamed to the same name
     */
    var repeatNames = true
    
    var renamePackages = true
    var removePackages = true
    
    var renameClasses = true
    var renameMethods = true
    var renameFields = true
    var renameLocals = true
    
    var packageSupplier = DEFAULT_SUPPLIER
    var classSupplier = DEFAULT_SUPPLIER
    var methodSupplier = DEFAULT_SUPPLIER
    var fieldSupplier = DEFAULT_SUPPLIER
    var localSupplier = DEFAULT_SUPPLIER
    
    var mappings = HashMap<String, String>()
    
    override fun transform(archive: JavaArchive) {
        generateMappings(archive)
        applyMappings(archive)
        VirtualClassPath.reload()
    }
    
    private fun generateMappings(archive: JavaArchive) {
        mappings = MappingsGenerator(archive).generateMappings()
    }
    
    private fun applyMappings(archive: JavaArchive) {
        val refactorer = Refactorer(archive, mappings)
        refactorer.refactor()
    }
    
    override fun loadConfig(config: JsonConfig) {
        repeatNames = config.getBoolean("repeatnames", true)
        
        renamePackages = config.getBoolean("packages", false)
        removePackages = config.getBoolean("removepackages", !renamePackages)
        
        renameClasses = config.getBoolean("classes", true)
        renameMethods = config.getBoolean("methods", true)
        renameFields = config.getBoolean("fields", true)
        renameLocals = config.getBoolean("locals", false)
        
        if (renamePackages)
            packageSupplier = config["supplier.packages"] ?: DEFAULT_SUPPLIER
        if (renameClasses)
            classSupplier = config["supplier.classes"] ?: DEFAULT_SUPPLIER
        if (renameMethods)
            methodSupplier = config["supplier.methods"] ?: DEFAULT_SUPPLIER
        if (renameFields)
            fieldSupplier = config["supplier.fields"] ?: DEFAULT_SUPPLIER
        if (renameLocals)
            localSupplier = config["supplier.locals"] ?: DEFAULT_SUPPLIER
        
        if (removePackages && renamePackages) {
            warn("Renaming packages is not supported when removing packages. Defaulting to removing packages.")
            renamePackages = false
        }
        
    }
    
}

/**
 * Checks if a field is renamable. A field can't be renamed if it starts with a ``$``.
 */
fun FieldNode.isRenamable() = !name.startsWith('$')

/**
 * Checks if a method is renamable.
 */
fun MethodNode.isRenamable(owner: ClassWrapper, mappings: HashMap<String, String>) =
    !accessWrapper.isNative()
        && !name.startsWith('<')
        && "main" != name && "premain" != name
        && !(owner.isEnum() && accessWrapper.isStatic() && ("values" == name || "valueOf" == name))
        && "${owner.name}.$name$desc" !in mappings
        && owner.superClasses.none { it.getMethod(name, desc) != null }