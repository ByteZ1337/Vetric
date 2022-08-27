import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `java-gradle-plugin`
    `kotlin-dsl`
    kotlin("jvm") version "1.7.10"
    `maven-publish`
    id("com.gradle.plugin-publish") version "0.14.0"
}

group = "xyz.xenondevs.vetric"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    mavenLocal()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("xyz.xenondevs.vetric:vetric-core:0.1-SNAPSHOT")
}