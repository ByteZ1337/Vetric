package xyz.xenondevs.vetric.cli.command

import xyz.xenondevs.vetric.Vetric
import xyz.xenondevs.vetric.cli.terminal.Terminal
import xyz.xenondevs.vetric.cli.terminal.info
import java.util.concurrent.CountDownLatch
import kotlin.concurrent.thread

object CommandManager {
    
    private var commandThread: Thread? = null
    
    fun startListening() {
        val latch = CountDownLatch(1)
        commandThread = thread(name = "Command Handler", isDaemon = true) {
            while (!Vetric.exit) {
                if (latch.count != 0L)
                    latch.countDown()
                val line = Terminal.readLine()
                if (line == "exit")
                    Vetric.exit = true
                else
                    info("Unknown command: $line")
            }
            println("Goodbye")
            Terminal.close()
        }
        latch.await()
    }
    
}