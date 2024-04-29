pluginManagement {
    repositories {
        gradlePluginPortal()
        maven ("https://maven.fabricmc.net/")
    }
}

rootProject.name = "ViaFabric"

include("viafabric-mc189")
include("viafabric-mc1122")
include("viafabric-mc1144")
include("viafabric-mc1152")
include("viafabric-mc1165")
include("viafabric-mc1171")
include("viafabric-mc1182")
include("viafabric-mc1194")
include("viafabric-mc1201")
include("viafabric-mc1204")
include("viafabric-mc1206")

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}
