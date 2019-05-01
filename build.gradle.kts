import org.apache.tools.ant.filters.ReplaceTokens

plugins {
    id("java")
    id("net.minecrell.licenser") version "0.4.1"
    id("fabric-loom") version "0.2.2-SNAPSHOT"
    id("com.palantir.git-version") version "0.12.0-rc2"
}

group = "com.github.creeper123123321.viafabric"
val gitVersion: groovy.lang.Closure<Any> by extra
version = "0.1.0-SNAPSHOT+" + try {
    gitVersion()
} catch (e: Exception) {
    "unknown"
}
extra.set("archivesBaseName", "ViaFabric")
description = "Client-side and server-side ViaVersion for Fabric"

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
    maven(url = "http://server.bbkr.space:8081/artifactory/libs-snapshot")
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
    // transitive = false, viabackwards-core because Guava is conflicting on runClient
    compile("us.myles:viaversion:2.0.2-SNAPSHOT") { isTransitive = false }
    include("us.myles:viaversion:2.0.2-SNAPSHOT")
    compile("de.gerrygames:viarewind-core:1.4.0") { isTransitive = false }
    include("de.gerrygames:viarewind-core:1.4.0")
    compile("nl.matsv:viabackwards-core:3.0.0-19w11b") { isTransitive = false } // todo update
    include("nl.matsv:viabackwards-core:3.0.0-19w11b")

    compileOnly("com.google.code.findbugs:jsr305:3.0.2")

    minecraft("com.mojang:minecraft:1.14")
    mappings("net.fabricmc:yarn:1.14+build.3")
    modCompile("net.fabricmc:fabric-loader:0.4.5+build.140")

    modCompile("net.fabricmc:fabric:0.2.7+build.127")

    modCompile("io.github.cottonmc:cotton-client-commands:0.3.1+1.14-SNAPSHOT")
    include("io.github.cottonmc:cotton-client-commands:0.3.1+1.14-SNAPSHOT")
}

minecraft {
}

// Loom will automatically attach sourcesJar to a RemapSourcesJar task and to the "build" task
// if it is present.
// If you remove this task, sources will not be generated.
tasks.register<Jar>("sourcesJar") {
    dependsOn(tasks.getByName("classes"))
    archiveClassifier.set("sources")
    from(sourceSets.getByName("main").allSource)
}
