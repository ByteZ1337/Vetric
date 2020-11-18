package xyz.xenondevs.obfuscator.jvm

import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassReader.SKIP_FRAMES
import xyz.xenondevs.obfuscator.Obfuscator

// TODO rework library loading
object ClassPath {

    val libraries = ArrayList<Library>()
    val wrappers = HashMap<String, ClassWrapper>()
    val inheritanceTree = HashMap<String, InheritanceTree>()

    fun loadJar(jar: JavaArchive) {
        // Note: this does not waste a lot of memory because the jvm just
        // saves a reference of the ClassWrapper and doesn't copy the object.
        jar.classes.forEach { wrappers[it.name] = it }
    }


    fun loadLibrary(library: Library) {
        libraries += library
        loadJar(library)
    }

    fun reset() {
        libraries.clear()
        reload()
    }

    fun reload() {
        wrappers.clear()
        inheritanceTree.clear()
        libraries.forEach { jar ->
            jar.classes.forEach { wrappers[it.name] = it }
        }
        loadJar(Obfuscator.input)
        System.gc()
    }

    fun getClassWrapper(name: String): ClassWrapper {
        try {
            wrappers[name]?.let { return it }

            // The ClassWrapper was not found in the cache.
            val wrapper = ClassWrapper("${name.replace('.', '/')}.class").also {
                ClassReader(name).accept(it, SKIP_FRAMES)
            }
            wrappers[wrapper.name] = wrapper
            return wrapper
        } catch (exception: Exception) {
            error("$name not found! Did you add all dependencies?")
        }
    }

    fun buildJarTree(jar: JavaArchive) {
        println("Building inheritance tree... This might take a while.")
        jar.classes.forEach(this::getTree)
        println("Done")
    }

    fun getTree(name: String, vararg knownSubClasses: String = emptyArray()) =
            getTree(getClassWrapper(name), *knownSubClasses)

    fun getTree(wrapper: ClassWrapper, vararg knownSubClasses: String = emptyArray()): InheritanceTree {
        if (!inheritanceTree.containsKey(wrapper.name)) {
            val tree = InheritanceTree(wrapper)
            tree.subClasses.addAll(knownSubClasses)

            wrapper.superName?.let { superName ->
                tree.parentClasses += superName
                getTree(getClassWrapper(superName), wrapper.name)
            }
            wrapper.interfaces?.let { interfaces ->
                interfaces.forEach { interf ->
                    tree.parentClasses += interf
                    getTree(getClassWrapper(interf), wrapper.name)
                }
            }

            inheritanceTree[wrapper.name] = tree
        }

        val tree = inheritanceTree[wrapper.name]!!
        tree.subClasses.addAll(knownSubClasses)
        return tree
    }

}