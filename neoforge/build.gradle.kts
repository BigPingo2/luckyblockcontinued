plugins {
    kotlin("jvm") version "1.9.24"
    id("net.neoforged.gradle.userdev") version "7.0.120"
}

group = "mod.lucky"
version = rootProject.extra["mod_version"] as String

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

repositories {
    mavenCentral()
    maven("https://maven.neoforged.net/releases")
}

dependencies {
    implementation(project(":common"))
    implementation(kotlin("stdlib"))
    implementation("net.neoforged:neoforge:${rootProject.extra["neo_version"]}")
}

tasks.processResources {
    filesMatching("META-INF/neoforge.mods.toml") {
        expand("version" to project.version)
    }
}
