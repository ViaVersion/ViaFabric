pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        maven ("https://maven.fabricmc.net/")
    }
}

rootProject.name = "ViaFabric"

include("viafabric-mc189")
include("viafabric-mc1122")

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}
