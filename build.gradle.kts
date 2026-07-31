plugins {
    id("java-library")

    alias(libs.plugins.lombok)
    alias(libs.plugins.shadow)
}

repositories {
    mavenLocal()
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.extendedclip.com/releases/")
    maven("https://repo.okaeri.cloud/releases")
    maven("https://repo.vulpine.top/repository/maven-open/")
}

dependencies {
    implementation(libs.okaeri)
    implementation(libs.okaeri.serdes)
    implementation(libs.bstats)
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
        shade("top.vulpine.commons", "commons")
    }

    build {
        dependsOn(shadowJar)
    }
}