package xyz.xenondevs.vetric.cli.command.impl

import xyz.xenondevs.vetric.cli.command.Command
import xyz.xenondevs.vetric.cli.command.CommandManager
import xyz.xenondevs.vetric.cli.terminal.Terminal

object HelpCommand : Command("help", "Display all commands") {
    
    override fun execute(args: List<String>) =
        CommandManager.commands.forEach {
            Terminal.info("${it.name}: ${it.description}")
        }
}