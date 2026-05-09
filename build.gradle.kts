plugins {
    id("java-library")

    alias(libs.plugins.lombok)
    alias(libs.plugins.shadow)
}

repositories {
    mavenLocal()
    mavenCentral()
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://oss.sonatype.org/content/groups/public/")
    maven("https://oss.sonatype.org/content/repositories/snapshots/")
    maven("https://repo.extendedclip.com/releases/")
}

dependencies {
    implementation(libs.minimessage)
    implementation(libs.minimessage.legacy)

    compileOnly(libs.spigot)
    compileOnly(libs.bungeecord.chat)
    compileOnly(libs.papi)
}

group = "top.vulpine"
val packageName = "simpleLobby"
version = "1.2.2"
description = "SimpleLobby"

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

tasks {
    shadowJar {
        archiveFileName.set("${project.name}-${project.version}.jar")

        val basePackage = "${project.group}.${packageName}.libs"
        fun shade(original: String, shaded: String) {
            relocate(original, "${basePackage}.${shaded}")
        }

        shade("net.kyori.adventure", "adventure")
    }
}