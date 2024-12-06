pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        maven ("https://maven.fabricmc.net/")
        maven ("https://repo.legacyfabric.net/repository/legacyfabric/")
    }
}

rootProject.name = "ViaFabric"

include("viafabric-mc189")
include("viafabric-mc1122")

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.9.0"
}
