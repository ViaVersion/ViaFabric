import org.apache.tools.ant.filters.ReplaceTokens
import java.util.function.Function as JavaFunction

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
    maven(url = "https://dl.bintray.com/legacy-fabric/Legacy-Fabric-Maven")
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

    // Use 1.8.9 Legacy Fabric https://github.com/Legacy-Fabric/fabric-example-mod/blob/master/gradle.properties
    implementation("com.google.guava:guava:23.5-jre")
    minecraft("com.mojang:minecraft:1.8.9")
    mappings("net.fabricmc:yarn:1.8.9+build.202007011615:v2")
    modCompile("net.fabricmc:fabric-loader-1.8.9:0.8.2+build.202004131640") {
        exclude(module = "guava")
    }

    modImplementation("net.fabricmc.fabric-api:fabric-api:0.2.7-1.8.9")

    //modImplementation("io.github.cottonmc:cotton-client-commands:1.0.0+1.15.2")
    //include("io.github.cottonmc:cotton-client-commands:1.0.0+1.15.2")
}

minecraft {
    this.intermediaryUrl = JavaFunction {
        "https://dl.bintray.com/legacy-fabric/Legacy-Fabric-Maven/net/fabricmc/intermediary/$it/intermediary-$it-v2.jar"
    }
}

license {
    include("**/*.java")
}
