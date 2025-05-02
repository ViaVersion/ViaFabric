pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        maven ("https://maven.fabricmc.net/")
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.9.0"
}

if (!file(".git").exists()) {
    val errorText = """

        =====================[ ERROR ]=====================
         The ViaFabric project directory is not a properly cloned Git repository.

         In order to build ViaFabric from source you must clone
         the ViaFabric repository using Git, not download a code
         zip from GitHub.

         Built ViaFabric jars are available for download at
         https://ci.viaversion.com/view/Platforms/job/ViaFabric/
        ===================================================
    """.trimIndent()
    error(errorText)
}

rootProject.name = "ViaFabric"

include("viafabric-mc1144")
include("viafabric-mc1152")
include("viafabric-mc1165")
include("viafabric-mc1171")
include("viafabric-mc1182")
include("viafabric-mc1194")
include("viafabric-mc1201")
include("viafabric-mc1206")
include("viafabric-mc1215")
