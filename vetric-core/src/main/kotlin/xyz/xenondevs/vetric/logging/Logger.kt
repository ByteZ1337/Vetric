package xyz.xenondevs.vetric.logging

interface Logger {
    
    fun debug(message: String, vararg args: Any)
    
    fun info(message: String, vararg args: Any)
    
    fun warn(message: String, vararg args: Any)
    
    fun error(message: String, vararg args: Any)
    
    fun critical(message: String, vararg args: Any)
    
}

internal object DiscardingLogger : Logger {
    
    override fun debug(message: String, vararg args: Any) = Unit
    
    override fun info(message: String, vararg args: Any) = Unit
    
    override fun warn(message: String, vararg args: Any) = Unit
    
    override fun error(message: String, vararg args: Any) = Unit
    
    override fun critical(message: String, vararg args: Any) = Unit
    
}