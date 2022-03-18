package xyz.xenondevs.vetric.utils

import org.objectweb.asm.tree.InsnList
import org.objectweb.asm.tree.MethodNode
import xyz.xenondevs.bytebase.jvm.ClassWrapper
import xyz.xenondevs.bytebase.jvm.JavaArchive

/**
 * Class containing the parents of an instruction.
 *
 * - JavaArchive
 * - Class
 * - Method
 * - InsnList
 */
class InstructionParent(val jar: JavaArchive, val clazz: ClassWrapper, val method: MethodNode, val insnList: InsnList)