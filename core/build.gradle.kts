plugins {
    java
    kotlin("jvm") version "1.5.0"
}

group = "xyz.xenondevs.vetric"
version = "0.1"
description = "vetric-core"
java.sourceCompatibility = JavaVersion.VERSION_16

repositories {
    mavenCentral()
    maven {
        url = uri("https://jitpack.io")
    }
}

dependencies {
    implementation("org.ow2.asm:asm:9.1")
    implementation("org.ow2.asm:asm-commons:9.1")
    implementation("org.ow2.asm:asm-util:9.1")
    implementation("org.ow2.asm:asm-tree:9.1")
    implementation("com.google.code.gson:gson:2.8.7")
    implementation("com.github.ByteZ1337:ByteBase:34c0bc8e48")
    implementation(kotlin("stdlib"))
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.6.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}