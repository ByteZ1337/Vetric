package xyz.xenondevs.vetric.jvm

data class InheritanceTree internal constructor(val wrapper: ClassWrapper) {
    val parentClasses = HashSet<String>()
    val subClasses = HashSet<String>()
}