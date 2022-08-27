package xyz.xenondevs.vetric.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.get
import org.gradle.kotlin.dsl.register
import xyz.xenondevs.vetric.gradle.task.RunVetricExtension
import xyz.xenondevs.vetric.gradle.task.RunVetricTask
import java.io.File

class VetricGradlePlugin : Plugin<Project> {
    
    override fun apply(target: Project) {
        target.pluginManager.apply(JavaPlugin::class.java)
        target.repositories.mavenLocal {
            metadataSources {
                mavenPom()
                artifact()
            }
        }
        val extension = target.extensions.create<RunVetricExtension>("vetric")
        val vetricTask = target.tasks.register<RunVetricTask>("vetric") {
            dependsOn(target.tasks["jar"])
        }.apply {
            configure {
                this.configFile.set(extension.configFile.get())
                this.inputFile.set(extension.inputFile.map(::File).get())
                this.outputFile.set(extension.outputFile.map(::File).get())
                this.autoloadDeps.set(extension.autoloadDeps.getOrElse(false))
            }
        }
        
        target.tasks["assemble"].dependsOn(vetricTask)
    }
    
}