/*
 * This file was generated by the Gradle 'init' task.
 */

plugins {
    java
    `maven-publish`
}

repositories {
    mavenLocal()
    maven {
        url = uri("https://repo.maven.apache.org/maven2/")
    }
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.5.10")
    testImplementation("org.jetbrains.kotlin:kotlin-test:1.5.10")
}

group = "xyz.xenondevs.vetric"
version = "0.1"
description = "Vetric"
java.sourceCompatibility = JavaVersion.VERSION_16

publishing {
    publications.create<MavenPublication>("maven") {
        from(components["java"])
    }
}
