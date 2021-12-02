package xyz.xenondevs.vetric.cli.terminal

import org.fusesource.jansi.Ansi
import org.jline.reader.LineReader
import org.jline.reader.LineReaderBuilder
import org.jline.terminal.TerminalBuilder
import xyz.xenondevs.vetric.Vetric
import xyz.xenondevs.vetric.logging.Logger
import java.text.SimpleDateFormat
import java.util.*
import org.jline.terminal.Terminal as JLineTerminal

@Suppress("MemberVisibilityCanBePrivate")
object Terminal : JLineTerminal by TerminalBuilder.terminal(), Logger {
    
    private val TIME_FORMAT = SimpleDateFormat("HH:mm:ss")
    
    private val PREFIX = ansi {
        bold()
        fgBrightCyan()
        a("\uD835\uDCE5/> ") // Fancy V
        reset()
    }
    
    private val lineReader: LineReader = LineReaderBuilder.builder().terminal(this).build()
    
    fun readLine(): String = readLine(PREFIX)
    
    fun readLine(prompt: String): String {
        return lineReader.readLine(prompt)
    }
    
    fun log(message: String, level: LogLevel) {
        val thread = Thread.currentThread().name
        val time = TIME_FORMAT.format(Date())
        val formattedMessage = ansi {
            a(level.formatting)
            a("[$time] ")
            a("[$thread | $level] ")
            if (level == LogLevel.INFO)
                reset()
            a(message)
            reset()
        }
        lineReader.printAbove(formattedMessage)
    }
    
    override fun debug(message: String, vararg args: Any) {
        if (Vetric.debug)
            log(message.format(args = args), LogLevel.DEBUG)
    }
    
    override fun info(message: String, vararg args: Any) = log(message.format(args = args), LogLevel.INFO)
    
    override fun warn(message: String, vararg args: Any) = log(message.format(args = args), LogLevel.WARNING)
    
    override fun error(message: String, vararg args: Any) = log(message.format(args = args), LogLevel.ERROR)
    
    override fun critical(message: String, vararg args: Any) = log(message.format(args = args), LogLevel.CRITICAL)
    
}

fun ansi(builder: Ansi.() -> Unit): String {
    val ansi = Ansi.ansi()
    ansi.builder()
    return ansi.toString()
}