package xyz.xenondevs.vetric.cli.command.impl

import xyz.xenondevs.vetric.cli.command.Command
import xyz.xenondevs.vetric.cli.command.CommandManager
import xyz.xenondevs.vetric.logging.info

object HelpCommand : Command("help", "Display all commands") {
    
    override fun execute(args: List<String>) =
        CommandManager.commands.forEach {
            info("${it.name}: ${it.description}")
        }
}