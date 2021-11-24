package xyz.xenondevs.vetric

import org.apache.commons.cli.*
import org.fusesource.jansi.AnsiConsole
import xyz.xenondevs.vetric.cli.command.CommandManager
import xyz.xenondevs.vetric.config.FileConfigSupplier
import xyz.xenondevs.vetric.config.VetricConfig
import java.io.File
import kotlin.concurrent.thread
import kotlin.system.exitProcess

object Launcher {
    
    private val START_OPTIONS: Options = Options()
        .addOption("h", "help", false, "Show the help menu")
        .addOption("v", "version", false, "Show the version")
        .addOption("d", "debug", false, "Enable debug mode")
        .addOption(
            Option.builder("cfg")
                .longOpt("config")
                .hasArg()
                .type(PatternOptionBuilder.FILE_VALUE)
                .desc("Path to the config file to use")
                .build()
        )
    
    @JvmStatic
    fun main(args: Array<String>) {
        AnsiConsole.systemInstall() // Install the Jansi console to use ANSI codes
        Thread.currentThread().name = "Vetric Main"
        CommandManager.startListening()
        Vetric.run(parseArgs(args))
    }
    
    private fun parseArgs(args: Array<String>): VetricConfig {
        val cl: CommandLine = DefaultParser().parse(START_OPTIONS, args)
        
        // Check if the help menu was requested
        if (cl.hasOption("h")) {
            val formatter = HelpFormatter()
            formatter.printHelp("java -jar Vetric.jar [-cfg <path>]", START_OPTIONS)
            exitProcess(0)
        }
        
        // Check if the version was requested
        if (cl.hasOption("v")) {
            println("Vetric v${Vetric.VERSION} running on Java " + System.getProperty("java.version"))
            exitProcess(0)
        }
        
        // Enable debug mode
        if (cl.hasOption("d"))
            Vetric.debug = true
        
        // Get the config file or use the default path at "./config.json"
        val file: File =
            if (cl.hasOption("cfg")) cl.getParsedOptionValue("cfg") as File
            else File("config.json")
        
        check(file.exists()) { "Config file does not exist." }
        check(file.isFile) { "Config file is not a file." }
        check(file.canRead()) { "Config file is not readable." }
        
        return VetricConfig(FileConfigSupplier(file))
    }
    
}