import org.apache.tools.ant.filters.ReplaceTokens

plugins {
    id("java")
    id("net.minecrell.licenser") version "0.4.1"
    id("fabric-loom") version "0.2.6-SNAPSHOT"
    id("com.palantir.git-version") version "0.12.0-rc2"
}

group = "com.github.creeper123123321.viafabric"
val gitVersion: groovy.lang.Closure<Any> by extra
version = "0.2.0-SNAPSHOT+" + try {
    gitVersion()
} catch (e: Exception) {
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
    compile("us.myles:viaversion:3.0.0-SNAPSHOT") { isTransitive = false }
    //include("us.myles:viaversion:3.0.0-SNAPSHOT")

    compileOnly("com.google.code.findbugs:jsr305:3.0.2")

    // Use release, probably intermediary will make it work on snapshots
    minecraft("com.mojang:minecraft:1.14.4")
    mappings("net.fabricmc:yarn:1.14.4+build.15")
    modCompile("net.fabricmc:fabric-loader:0.6.4+build.169")

    modCompile("net.fabricmc.fabric-api:fabric-api:0.4.1+build.245-1.14")

    modCompile("io.github.cottonmc:cotton-client-commands:0.4.2+1.14.3-SNAPSHOT")
    include("io.github.cottonmc:cotton-client-commands:0.4.2+1.14.3-SNAPSHOT")
}

minecraft {
}

license {
    include("**/*.java")
}
