package xyz.xenondevs.vetric.logging

import xyz.xenondevs.vetric.Vetric

interface Logger {
    
    fun debug(message: String, vararg args: Any)
    
    fun info(message: String, vararg args: Any)
    
    fun warn(message: String, vararg args: Any)
    
    fun error(message: String, vararg args: Any)
    
    fun critical(message: String, vararg args: Any)
    
}

fun debug(message: String, vararg args: Any) = Vetric.logger?.debug(message, args = args)

fun info(message: String, vararg args: Any) = Vetric.logger?.info(message, args = args)

fun warn(message: String, vararg args: Any) = Vetric.logger?.warn(message, args = args)

fun err(message: String, vararg args: Any) = Vetric.logger?.error(message, args = args) // Can't use error because of conflict with Kotlin's error function

fun critical(message: String, vararg args: Any) = Vetric.logger?.critical(message, args = args)