pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        maven("https://maven.fabricmc.net/")
    }

    plugins {
        id("org.ajoberstar.grgit") version "5.3.2"
        id("net.fabricmc.fabric-loom") version "1.17-SNAPSHOT"
        id("com.github.ben-manes.versions") version "0.53.0"
        id("me.modmuss50.mod-publish-plugin") version "2.2.0"
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

rootProject.name = "ViaFabric"

include("viafabric-mc26-1")
include("viafabric-mc26-2")
