package xyz.xenondevs.vetric.cli.terminal

import org.fusesource.jansi.Ansi.Attribute

enum class LogLevel(val formatting: String) {
    DEBUG(ansi { fgCyan().a(Attribute.ITALIC) }),
    INFO(""),
    WARNING(ansi { fgBrightYellow() }),
    ERROR(ansi { fgBrightRed() }),
    CRITICAL(ansi { fgRed().a(Attribute.UNDERLINE) }),
}