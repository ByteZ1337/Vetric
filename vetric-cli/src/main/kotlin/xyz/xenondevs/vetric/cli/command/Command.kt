package xyz.xenondevs.vetric.cli.command

abstract class Command(val name: String, val description: String) : Comparable<Command> {
    
    abstract fun execute(args: List<String>)
    
    override fun compareTo(other: Command) =
        name.compareTo(other.name)
    
}