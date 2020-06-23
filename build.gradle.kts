import org.apache.tools.ant.filters.ReplaceTokens

plugins {
    id("java")
    id("net.minecrell.licenser") version "0.4.1"
    id("fabric-loom") version "0.2.7-SNAPSHOT"
    id("com.palantir.git-version") version "0.12.0-rc2"
}

group = "com.github.creeper123123321.viafabric"
val gitVersion: groovy.lang.Closure<Any> by extra
version = "0.2.3-SNAPSHOT+" + try {
    gitVersion()
} catch (e: Exception) {
    "unknown"
} + "-mc-1.16"
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
    implementation("us.myles:viaversion:3.0.1-SNAPSHOT") { isTransitive = false }

    // Use 1.16 snapshot, probably intermediary will make it work on further versions
    // https://modmuss50.me/fabric.html?&version=1.16
    minecraft("com.mojang:minecraft:1.16")
    mappings("net.fabricmc:yarn:1.16+build.1:v2")
    modImplementation("net.fabricmc:fabric-loader:0.8.8+build.202")

    modImplementation("net.fabricmc.fabric-api:fabric-api:0.13.1+build.370-1.16")

    modImplementation("io.github.cottonmc:cotton-client-commands:1.0.0+1.15.2")
    include("io.github.cottonmc:cotton-client-commands:1.0.0+1.15.2")
}

minecraft {
}

license {
    include("**/*.java")
}
