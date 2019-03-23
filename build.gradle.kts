import org.apache.tools.ant.filters.ReplaceTokens
import java.net.URI

plugins {
    id("java")
    id("net.minecrell.licenser") version "0.4.1"
    id("fabric-loom") version "0.2.0-SNAPSHOT"
    id("com.palantir.git-version") version "0.12.0-rc2"
}

group = "com.github.creeper123123321.viafabric"
val gitVersion: groovy.lang.Closure<Any> by extra
version = "0.1.0-SNAPSHOT+" + gitVersion()
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
    maven { url = URI.create("https://repo.viaversion.com/") }
    maven { url = URI.create("https://oss.sonatype.org/content/repositories/snapshots") }
    maven { url = URI.create("https://maven.fabricmc.net/") }
}


tasks.getByName<ProcessResources>("processResources").apply {
    filter<ReplaceTokens>("tokens" to mapOf(
            "version" to project.property("version"),
            "description" to project.property("description")
    ))
}

val shade by configurations.creating
configurations.getByName("compile").extendsFrom(shade)

dependencies {
    // transitive = false, viabackwards-core because Guava is conflicting on runClient
    shade("us.myles:viaversion:2.0.0-19w12b") { isTransitive = false }
    shade("de.gerrygames:viarewind-core:1.4.0") { isTransitive = false }
    shade("nl.matsv:viabackwards-core:3.0.0-19w11b") { isTransitive = false }

    compileOnly("com.google.code.findbugs:jsr305:3.0.2")

    minecraft("com.mojang:minecraft:19w12b")
    mappings("net.fabricmc:yarn:19w12b.3")
    modCompile("net.fabricmc:fabric-loader:0.3.7.109")

    modCompile("net.fabricmc:fabric:0.2.5.114")
}

tasks.named<Jar>("jar") {
    shade.forEach { dep ->
        from(project.zipTree(dep)) {
            exclude("us/myles/ViaVersion/BungeePlugin.class")
            exclude("us/myles/ViaVersion/SpongePlugin.class")
            exclude("us/myles/ViaVersion/VelocityPlugin.class")
            exclude("us/myles/ViaVersion/ViaVersionPlugin.class")
            // exclude("us/myles/ViaVersion/sponge/**") needed for viabackwards version check
            exclude("us/myles/ViaVersion/bukkit/**")
            exclude("us/myles/ViaVersion/bungee/**")
            exclude("us/viaversion/libs/javassist/**")
            exclude("mcmod.info")
            exclude("plugin.yml")
            exclude("bungee.yml")
            exclude("velocity-plugin.json")
        }
    }
}

minecraft {
}

// Loom will automatically attach sourcesJar to a RemapSourcesJar task and to the "build" task
// if it is present.
// If you remove this task, sources will not be generated.
tasks.register<Jar>("sourcesJar") {
    dependsOn(tasks.getByName("classes"))
    classifier = "sources"
    from(sourceSets.getByName("main").allSource)
}