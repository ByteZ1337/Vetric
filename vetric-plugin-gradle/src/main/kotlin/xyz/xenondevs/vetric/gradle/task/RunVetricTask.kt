package xyz.xenondevs.vetric.gradle.task

import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction
import xyz.xenondevs.vetric.Vetric
import xyz.xenondevs.vetric.config.FileConfigSupplier
import xyz.xenondevs.vetric.config.VetricConfig
import xyz.xenondevs.vetric.gradle.GradleLogger
import xyz.xenondevs.vetric.jvm.Library

abstract class RunVetricTask : DefaultTask() {
    
    @get:InputFile
    abstract val configFile: RegularFileProperty
    
    @get:InputFile
    abstract val inputFile: RegularFileProperty
    
    @get:InputFile
    abstract val outputFile: RegularFileProperty
    
    @get:Input
    @get:Optional
    abstract val autoloadDeps: Property<Boolean>
    
    @TaskAction
    fun runVetric() {
        val vetric = Vetric(GradleLogger(), true)
        val config = VetricConfig(FileConfigSupplier(configFile.get().asFile), vetric)
        config.input = inputFile.get().asFile
        config.output = outputFile.get().asFile
        if (autoloadDeps.getOrElse(false)) {
            val libraries = ArrayList<Library>()
            project.configurations.getByName("compileClasspath").incoming.artifacts.artifactFiles.files.forEach { libFile ->
                libraries.add(Library(libFile, isExtracted = false))
            }
            config.libraries = libraries
        }
        vetric.run(config)
    }
    
}