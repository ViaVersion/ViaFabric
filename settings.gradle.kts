pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        maven("https://maven.fabricmc.net/")
    }
}

rootProject.name = "ViaFabric"

include("viafabric-mc1144")
include("viafabric-mc1152")
include("viafabric-mc1165")
include("viafabric-mc1171")
include("viafabric-mc1182")
include("viafabric-mc1194")
include("viafabric-mc1206")
include("viafabric-mc1219")

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}
