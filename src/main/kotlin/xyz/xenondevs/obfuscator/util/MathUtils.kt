package xyz.xenondevs.obfuscator.util

import java.security.SecureRandom
import kotlin.random.asKotlinRandom

object MathUtils {

    val RANDOM = SecureRandom().asKotlinRandom()
    
}