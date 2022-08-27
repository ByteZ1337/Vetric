package xyz.xenondevs.vetric.gradle

import xyz.xenondevs.vetric.logging.Logger

class GradleLogger : Logger {
    
    override fun critical(message: String, vararg args: Any) {
        println("[CRITICAL] $message")
    }
    
    override fun debug(message: String, vararg args: Any) {
        println("[DEBUG] $message")
    }
    
    override fun error(message: String, vararg args: Any) {
        println("[ERROR] $message")
    }
    
    override fun info(message: String, vararg args: Any) {
        println("[INFO] $message")
    }
    
    override fun warn(message: String, vararg args: Any) {
        println("[WARN] $message")
    }
}