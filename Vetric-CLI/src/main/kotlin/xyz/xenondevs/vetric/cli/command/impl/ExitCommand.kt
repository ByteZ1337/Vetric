package xyz.xenondevs.vetric.cli.command.impl

import xyz.xenondevs.vetric.Vetric
import xyz.xenondevs.vetric.cli.command.Command

object ExitCommand: Command("exit", "Exits the program") {
    
    override fun execute(args: List<String>) {
        Vetric.exit = true
    }
    
}