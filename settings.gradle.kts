pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        maven("https://maven.fabricmc.net/")
    }
}

rootProject.name = "ViaFabric"

include("viafabric-mc26-1")
include("viafabric-mc26-2")

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}
