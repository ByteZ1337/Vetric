package xyz.xenondevs.obfuscator.utils

import java.security.SecureRandom
import kotlin.random.asKotlinRandom

object MathUtils {
    
    val RANDOM = SecureRandom().asKotlinRandom()
    
}