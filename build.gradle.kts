import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.10"
    application
}

group = "me.plony"
version = "1.0-SNAPSHOT"

repositories {
    // ...
    mavenCentral()
    maven(url = "https://jitpack.io")
}

val exposedVersion = "0.40.1"

dependencies {
    implementation("net.kyori:adventure-text-minimessage:4.11.0")
    implementation("com.github.Minestom:Minestom:17ef1c2f57")
    implementation("com.github.Project-Cepi:KStom:d4d55dc")

    implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-dao:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")

    implementation("org.xerial:sqlite-jdbc:3.39.3.0")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "17"
}

application {
    mainClass.set("MainKt")
}