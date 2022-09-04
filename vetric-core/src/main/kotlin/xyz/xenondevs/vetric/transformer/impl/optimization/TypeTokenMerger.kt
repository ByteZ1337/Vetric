package xyz.xenondevs.vetric.transformer.impl.optimization

import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet
import org.objectweb.asm.Opcodes.*
import org.objectweb.asm.Type
import org.objectweb.asm.tree.AbstractInsnNode
import org.objectweb.asm.tree.FieldNode
import org.objectweb.asm.tree.InsnList
import org.objectweb.asm.tree.TypeInsnNode
import xyz.xenondevs.bytebase.asm.buildInsnList
import xyz.xenondevs.bytebase.jvm.ClassWrapper
import xyz.xenondevs.bytebase.jvm.JavaArchive
import xyz.xenondevs.bytebase.util.next
import xyz.xenondevs.bytebase.util.replaceRange
import xyz.xenondevs.vetric.transformer.Transformer
import xyz.xenondevs.vetric.transformer.TransformerPriority
import xyz.xenondevs.vetric.utils.section
import xyz.xenondevs.vetric.utils.swapTo

private const val TYPE_TOKEN_CLASS = "com/google/gson/reflect/TypeToken"
private val SIMPLE_REGEX = Regex("""^<L([^:;<>]+);>$""")

/**
 * TODO fix preexisting TypeToken classes
 */
class TypeTokenMerger : Transformer("TypeTokenMerger", TransformerPriority.HIGHEST) {
    
    override fun transform(jar: JavaArchive) {
        val (tokens, resolutions) = resolveTokens(jar)
    
        val outerClasses = assignOuterClasses(jar, tokens.keys)
    
        outerClasses.forEach { (clazz, innerTokens) ->
            val replacements = Object2ObjectLinkedOpenHashMap<IntRange, InsnList>()
            clazz.methods.forEach { method ->
                var i = 0
                val instructions = method.instructions
                instructions.forEach { insn ->
                    if (insn.checkForTypeToken(innerTokens)) {
                        val resolution = resolutions[tokens[(insn as TypeInsnNode).desc]!!]!!
                        replacements [i until i + 4] = resolution.instructions
                    }
                    ++i
                }
                if (replacements.size > 0) {
                    var currentOffset = 0
                    replacements.forEach { (range, toInsert) ->
                        instructions.replaceRange(range.first + currentOffset, range.last + currentOffset, toInsert)
                        currentOffset += toInsert.size() - (range.last - range.first)
                    }
                    replacements.clear()
                }
            }
        }
    
    }
    
    private fun resolveTokens(jar: JavaArchive): Pair<Map<String, String>, Map<String, Resolution>> { // tokens -> resolutions
        val tokens = Object2ObjectOpenHashMap<String, String>()
        val typeResolutions = Object2ObjectOpenHashMap<String, Resolution>()
        
        var complexCount = 0
        
        jar.classes.asSequence().filter { it.superName == TYPE_TOKEN_CLASS }.forEach { clazz ->
            val type = clazz.signature?.section('<', '>') ?: return@forEach
            if (type.startsWith("<L")) {
                tokens[clazz.name] = type
                if (type !in typeResolutions) {
                    var match: MatchResult? = null
                    typeResolutions[type] =
                        if (SIMPLE_REGEX.matchEntire(type)?.also { match = it } != null) {
                            SimpleResolution(match!!.groupValues[1])
                        } else {
                            ComplexResolution(clazz.signature, complexCount++)
                        }
                }
            }
        }
        
        return tokens to typeResolutions
    }
    
    private fun AbstractInsnNode.checkForTypeToken(tokens: Set<String>): Boolean {
        return opcode == NEW
            && (this as TypeInsnNode).desc in tokens
            && this.next(1)?.opcode == DUP
            && this.next(2)?.opcode == INVOKESPECIAL
            && this.next(3)?.opcode == INVOKEVIRTUAL
    }
    
    private fun assignOuterClasses(jar: JavaArchive, tokens: Set<String>): Map<ClassWrapper, Set<String>> {
        val outerClasses = Object2ObjectOpenHashMap<String, ClassWrapper>()
        jar.classes.asSequence().forEach { clazz ->
            val name = clazz.name
            
            for (token in tokens) {
                
                if (!token.startsWith(name))
                    continue
                
                if (token.length == name.length)
                    continue
                
                if (token !in outerClasses) {
                    outerClasses[token] = clazz
                    continue
                }
                
                if (outerClasses[token]!!.name.length > name.length)
                    continue
                
                outerClasses[token] = clazz
            }
        }
        return outerClasses.swapTo(::Object2ObjectOpenHashMap, ::ObjectLinkedOpenHashSet)
    }
    
    private abstract class Resolution(protected val type: String) {
        
        abstract val instructions: InsnList
        
    }
    
    private class SimpleResolution(type: String) : Resolution(type) {
        
        override val instructions = buildInsnList {
            ldc(Type.getObjectType(type))
        }
        
    }
    
    private class ComplexResolution(type: String, complexIdx: Int) : Resolution(type) {
        
        private val clazz = ClassWrapper("TypeToken$complexIdx.class").apply {
            name = fileName.removeSuffix(".class")
            signature = type
            superName = TYPE_TOKEN_CLASS
            access = ACC_PUBLIC or ACC_STATIC or ACC_FINAL
            version = V17
            fields.add(FieldNode(
                ACC_PUBLIC or ACC_STATIC or ACC_FINAL,
                "TYPE",
                "Ljava/lang/reflect/Type;",
                null,
                null)
            )
            getOrCreateClassInit().instructions = buildInsnList {
                new(name)
                dup()
                invokeSpecial(name, "<init>", "()V")
                invokeVirtual(name, "getType", "()Ljava/lang/reflect/Type;")
                putStatic(name, "TYPE", "Ljava/lang/reflect/Type;")
                _return()
            }
        }
        
        override val instructions = buildInsnList {
            getStatic(clazz.name, "TYPE", "Ljava/lang/reflect/Type;")
        }
        
    }
}