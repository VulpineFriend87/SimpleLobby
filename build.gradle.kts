import net.minecrell.pluginyml.paper.PaperPluginDescription

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

    compileOnly(libs.paper)
    compileOnly(libs.papi)
}

group = "top.vulpine"
val packageName = "simpleLobby"
version = "1.4.1"
description = "SimpleLobby"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
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

    commands {
        register("simplelobby") {
            description = "Main SimpleLobby command"
            aliases = listOf("sl", "slobby")
        }

        register("spawn") {
            description = "Teleports the executor to the spawn (if enabled)"
        }
    }
}