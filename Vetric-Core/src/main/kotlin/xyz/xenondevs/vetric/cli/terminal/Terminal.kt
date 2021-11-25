package xyz.xenondevs.vetric.cli.terminal

import org.fusesource.jansi.Ansi
import org.jline.reader.LineReader
import org.jline.reader.LineReaderBuilder
import org.jline.reader.Widget
import org.jline.terminal.TerminalBuilder
import xyz.xenondevs.vetric.Vetric
import java.text.SimpleDateFormat
import java.util.*
import kotlin.concurrent.thread
import org.jline.terminal.Terminal as JLineTerminal

@Suppress("MemberVisibilityCanBePrivate")
object Terminal : JLineTerminal by TerminalBuilder.terminal() {
    
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
    
    fun logFormatted(message: String, logLevel: LogLevel = LogLevel.INFO) {
        val thread = Thread.currentThread().name
        val time = TIME_FORMAT.format(Date())
        val formattedMessage = ansi {
            a(logLevel.formatting)
            a("[$time] ")
            a("[$thread | $logLevel] ")
            if (logLevel == LogLevel.INFO)
                reset()
            a(message)
            reset()
        }
        lineReader.printAbove(formattedMessage)
    }
    
}

fun ansi(builder: Ansi.() -> Unit): String {
    val ansi = Ansi.ansi()
    ansi.builder()
    return ansi.toString()
}

fun log(message: String, logLevel: LogLevel = LogLevel.INFO) = Terminal.logFormatted(message, logLevel)

fun log(message: String, logLevel: LogLevel = LogLevel.INFO, vararg args: Any) = Terminal.logFormatted(message.format(args = args), logLevel)

fun debug(message: String, vararg args: Any) {
    if (Vetric.debug)
        Terminal.logFormatted(message.format(args = args), LogLevel.DEBUG)
}

fun info(message: String, vararg args: Any) = Terminal.logFormatted(message.format(args = args), LogLevel.INFO)

fun warn(message: String, vararg args: Any) = Terminal.logFormatted(message.format(args = args), LogLevel.WARNING)

fun error(message: String, vararg args: Any) = Terminal.logFormatted(message.format(args = args), LogLevel.ERROR)

fun critical(message: String, vararg args: Any) = Terminal.logFormatted(message.format(args = args), LogLevel.CRITICAL)