package xyz.xenondevs.vetric.cli.command

import org.jline.reader.impl.completer.StringsCompleter
import xyz.xenondevs.vetric.Vetric
import xyz.xenondevs.vetric.cli.command.impl.ExitCommand
import xyz.xenondevs.vetric.cli.command.impl.HelpCommand
import xyz.xenondevs.vetric.cli.terminal.Terminal
import xyz.xenondevs.vetric.logging.warn
import java.util.concurrent.CountDownLatch
import kotlin.concurrent.thread

object CommandManager {
    
    val commands = sortedSetOf(ExitCommand, HelpCommand)
    
    private var commandThread: Thread? = null
    
    fun startListening() {
        val latch = CountDownLatch(1)
        commandThread = thread(name = "Command Handler", isDaemon = true) {
            while (!Vetric.exit) {
                if (latch.count != 0L)
                    latch.countDown()
                handleCommand(Terminal.readLine().trim())
            }
            println("Goodbye")
            Terminal.close()
        }
        latch.await()
    }
    
    private fun handleCommand(line: String) {
        val split = line.split(' ')
        val name = split[0]
        val args = split.drop(1)
        val command = commands.find { it.name.equals(name, ignoreCase = true)}
        if (command == null) {
            warn("Unknown command: $name. Type 'help' for help.")
            return
        }
        command.execute(args)
    }
    
    object CommandCompleter : StringsCompleter({ commands.map { it.name } })
    
}