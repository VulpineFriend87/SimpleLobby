plugins {
    id("java-library")

    alias(libs.plugins.lombok)
    alias(libs.plugins.shadow)
    alias(libs.plugins.pluginyml)
}

repositories {
    mavenLocal()
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.tcoded.com/releases")
    maven("https://repo.extendedclip.com/releases/")
    maven("https://repo.okaeri.cloud/releases")
    maven("https://repo.vulpine.top/repository/maven-open/")
}

dependencies {
    implementation(libs.okaeri)
    implementation(libs.okaeri.serdes)
    implementation(libs.bstats)
    implementation(libs.folialib)
    implementation(libs.commons)
    implementation(libs.actions)
    implementation(libs.lamp.common)
    implementation(libs.lamp.bukkit)

    compileOnly(libs.paper)
    compileOnly(libs.papi)
}

group = "top.vulpine"
val packageName = "simpleLobby"
version = "2.0"
description = "A lightweight Minecraft plugin for easy lobby management and customizable spawn actions."

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

tasks.withType<JavaCompile> {
    options.compilerArgs.add("-parameters")
}

tasks {
    jar {
        enabled = false
    }

    shadowJar {
        archiveFileName.set("${project.name}-${project.version}.jar")

        val basePackage = "${project.group}.${packageName}.libs"
        fun shade(original: String, shaded: String) {
            relocate(original, "${basePackage}.${shaded}")
        }

        shade("eu.okaeri", "okaeri")
        shade("org.bstats", "bstats")
        shade("com.tcoded.folialib", "folialib")
        shade("top.vulpine.commons", "commons")
        shade("top.vulpine.actions", "actions")
        shade("revxrsal.commands", "lamp")
    }

    build {
        dependsOn(shadowJar)
    }
}

bukkit {
    name = project.name
    description = project.description
    version = project.version.toString()
    apiVersion = "1.18"
    main = "${project.group}.${packageName}.${project.name}"

    author = "VulpineFriend87"
    website = "https://vulpine.top"
    foliaSupported = true

    softDepend = listOf("PlaceholderAPI")
}
