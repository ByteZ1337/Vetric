package xyz.xenondevs.vetric.transformer.impl.obfuscation.string.pool

import org.objectweb.asm.Opcodes.ACC_PUBLIC
import org.objectweb.asm.Opcodes.ACC_STATIC
import org.objectweb.asm.tree.FieldNode
import org.objectweb.asm.tree.LdcInsnNode
import xyz.xenondevs.bytebase.asm.buildInsnList
import xyz.xenondevs.bytebase.jvm.ClassWrapper
import xyz.xenondevs.bytebase.util.replace
import xyz.xenondevs.vetric.supplier.impl.AlphaSupplier
import xyz.xenondevs.vetric.transformer.impl.obfuscation.string.StringTransformer
import xyz.xenondevs.vetric.utils.filterTypeSub

object SingleStringPooler : StringTransformer("SingleStringPooler") {
    
    // TODO
    var useArrayPool = false
    
    override fun transform(clazz: ClassWrapper) {
        val strings = getStrings(clazz)
        if (strings.isEmpty()) return
        val overlapped = overlapStrings(strings)
        val lookupTable = generateIndices(overlapped, strings) // String -> start
        val pool = FieldNode(ACC_PUBLIC or ACC_STATIC, AlphaSupplier().randomString(), "Ljava/lang/String;", null, overlapped)
        
        clazz.fields.add(pool)
        clazz.methods.forEach {
            val insnList = it.instructions
            insnList.filterTypeSub<LdcInsnNode, String>(LdcInsnNode::cst).forEach { insn ->
                val value = insn.cst as String
                val start = lookupTable[value]!!
                val end = start + value.length
                insnList.replace(insn, buildInsnList {
                    getStatic(clazz.name, pool.name, "Ljava/lang/String;")
                    ldc(start)
                    ldc(end)
                    invokeVirtual("java/lang/String", "substring", "(II)Ljava/lang/String;")
                })
            }
        }
    }
    
    private fun getStrings(clazz: ClassWrapper): Set<String> {
        return clazz.methods
            .asSequence()
            .flatMap { it.instructions }
            .filterTypeSub<LdcInsnNode, String> { it.cst }
            .map { it.cst as String }
            .toSet()
    }
    
    private fun generateIndices(overlapped: String, strings: Set<String>): HashMap<String, Int> {
        val indices = hashMapOf<String, Int>()
        strings.forEach {
            val index = overlapped.indexOf(it)
            if (index == -1)
                throw IllegalStateException()
            indices[it] = index
        }
        return indices
    }
    
    private fun overlapStrings(set: Set<String>): String {
        val sorted = set.sortedByDescending(String::length)
        var string = ""
        sorted.forEach {
            if (it.isNotEmpty() && !string.contains(it)) {
                string = if (string.isEmpty()) it else overlapStrings(string, it)
            }
        }
        return string
    }
    
    private fun overlapStrings(string1: String, string2: String): String {
        val (longString, shortString) = sortStrings(string1, string2) // Might not be needed?
        
        var startIndex = longString.lastIndexOf(shortString[0])
        var length1 = findDifference(longString, shortString, startIndex)
        if (length1 == longString.length)
            return longString.take(startIndex) + shortString
        
        startIndex = shortString.lastIndexOf(longString[0])
        length1 = findDifference(shortString, longString, startIndex)
        if (length1 == shortString.length)
            return shortString + longString.drop(shortString.length - startIndex)
        
        return shortString + longString
    }
    
    private fun findDifference(string1: String, string2: String, startIndex: Int): Int {
        val length1 = string1.length
        val length2 = string2.length
        var index1 = startIndex
        var index2 = 0
        if (index1 != -1) {
            while (index1 < length1 && index2 < length2 && string1[index1] == string2[index2]) {
                ++index1
                ++index2
            }
        }
        return index1
    }
    
    private fun sortStrings(string1: String, string2: String): Pair<String, String> =
        if (string1.length > string2.length) string1 to string2 else string2 to string1
    
}