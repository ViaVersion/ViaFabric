import org.apache.tools.ant.filters.ReplaceTokens

plugins {
    id("java")
    id("net.minecrell.licenser") version "0.4.1"
    id("fabric-loom") version "0.4-SNAPSHOT"
    id("com.palantir.git-version") version "0.12.0-rc2"
}

group = "com.github.creeper123123321.viafabric"
val gitVersion: groovy.lang.Closure<String> by extra
val versionDetails: groovy.lang.Closure<com.palantir.gradle.gitversion.VersionDetails> by extra
version = "0.2.5-SNAPSHOT+" + try {
    val travisBranch: String? = System.getenv("TRAVIS_BRANCH") // version details doesn't work on travis
    gitVersion() + "-" + if (travisBranch.isNullOrBlank()) versionDetails().branchName else travisBranch
} catch (e: Exception) {
    e.printStackTrace()
    "unknown"
}
extra.set("archivesBaseName", "ViaFabric")
description = "Client-side and server-side ViaVersion implementation for Fabric"

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}
extra.set("sourceCompatibility", 1.8)
extra.set("targetCompatibility", 1.8)

repositories {
    mavenLocal()
    mavenCentral()
    jcenter()
    maven(url = "https://repo.viaversion.com/")
    maven(url = "https://oss.sonatype.org/content/repositories/snapshots")
    maven(url = "https://maven.fabricmc.net/")
    maven(url = "https://server.bbkr.space/artifactory/libs-snapshot")
    maven(url = "https://server.bbkr.space/artifactory/libs-release")
}


tasks.named<ProcessResources>("processResources") {
    filesMatching("fabric.mod.json") {
        filter<ReplaceTokens>("tokens" to mapOf(
                "version" to project.property("version"),
                "description" to project.property("description")
        ))
    }
}

dependencies {
    // transitive = false because Guava is conflicting on runClient
    implementation("us.myles:viaversion:3.0.2-SNAPSHOT") { isTransitive = false }

    // Use 1.14.4 release, probably intermediary will make it work on snapshots
    // https://modmuss50.me/fabric.html?&version=1.14.4
    minecraft("com.mojang:minecraft:1.14.4")
    mappings("net.fabricmc:yarn:1.14.4+build.16:v2")
    modImplementation("net.fabricmc:fabric-loader:0.8.2+build.194")

    modImplementation("net.fabricmc.fabric-api:fabric-api:0.13.1+build.257-1.14")

    modImplementation("io.github.cottonmc:cotton-client-commands:1.0.0+1.15.2")
    include("io.github.cottonmc:cotton-client-commands:1.0.0+1.15.2")
}

minecraft {
}

license {
    include("**/*.java")
}
