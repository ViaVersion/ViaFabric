pluginManagement {
    repositories {
        gradlePluginPortal()
        maven ("https://maven.fabricmc.net/")
    }
}

rootProject.name = "viafabric"

include("viafabric-mc18")
include("viafabric-mc114")
include("viafabric-mc115")
include("viafabric-mc116")
include("viafabric-mc117")
include("viafabric-mc118")
include("viafabric-mc119")


plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.4.0"
}
