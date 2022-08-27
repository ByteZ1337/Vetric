package xyz.xenondevs.vetric.gradle.task

import org.gradle.api.provider.Property
import java.io.File

abstract class RunVetricExtension {
    
    abstract val configFile: Property<File>
    
    abstract val inputFile: Property<String>
    
    abstract val outputFile: Property<String>
    
    abstract val autoloadDeps: Property<Boolean>
    
}