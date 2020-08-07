import org.apache.tools.ant.filters.ReplaceTokens
import java.util.function.Function as JavaFunction

plugins {
    id("java")
    id("net.minecrell.licenser") version "0.4.1"
    id("fabric-loom") version "0.4-SNAPSHOT"
    id("com.palantir.git-version") version "0.12.0-rc2"
    id("com.matthewprenger.cursegradle") version "1.4.0"
}

group = "com.github.creeper123123321.viafabric"
val gitVersion: groovy.lang.Closure<String> by extra
val versionDetails: groovy.lang.Closure<com.palantir.gradle.gitversion.VersionDetails> by extra

val travisBranch: String? = System.getenv("TRAVIS_BRANCH") // version details doesn't work on travis
val branch = if (!travisBranch.isNullOrBlank()) travisBranch else try {
    versionDetails().branchName
} catch (e: Exception) {
    "unknown"
}

version = "0.2.7-SNAPSHOT+" + try {
    gitVersion() + "-" + branch
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
    implementation("us.myles:viaversion:3.1.0-1.16.2-pre3") { isTransitive = false }
    include("us.myles:viaversion:3.1.0-1.16.2-pre3")
    include("org.yaml:snakeyaml:1.26")

    // Use 1.8.9 Legacy Fabric https://github.com/Legacy-Fabric/fabric-example-mod/blob/master/gradle.properties
    implementation("com.google.guava:guava:23.5-jre")
    minecraft("com.mojang:minecraft:1.8.9")
    mappings("net.fabricmc:yarn:1.8.9+build.202007011615:v2")
    modCompile("net.fabricmc:fabric-loader-1.8.9:0.8.2+build.202004131640") {
        exclude(module = "guava")
    }

    modImplementation("net.fabricmc.fabric-api:fabric-api:0.2.7-1.8.9")

    //modImplementation("io.github.cottonmc:cotton-client-commands:1.0.1+1.16-rc1")
    //include("io.github.cottonmc:cotton-client-commands:1.0.1+1.16-rc1")

    //modImplementation("com.extracraftx.minecraft:ProgrammerArtInjector:1.2.0")
    //include("com.extracraftx.minecraft:ProgrammerArtInjector:1.2.0")
}

if (!System.getenv()["curse_api_key"].isNullOrBlank() && branch.startsWith("mc-")) {
    defaultTasks("clean", "build", "curseforge")
} else {
    defaultTasks("clean", "build")
}

curseforge {
    apiKey = System.getenv()["curse_api_key"] ?: "undefined"
    project(closureOf<com.matthewprenger.cursegradle.CurseProject> {
        id = "391298"
        changelog = "A changelog can be found at https://github.com/ViaVersion/ViaFabric/commits"
        releaseType = "alpha"
        when (branch) {
            "mc-1.8" -> addGameVersion("1.8.9")
            "mc-1.14-1.15" -> {
                addGameVersion("1.14.4")
                addGameVersion("1.15.2")
            }
            "mc-1.16" -> {
                addGameVersion("1.16.1")
            }
        }
        addGameVersion("Fabric")
        mainArtifact(file("${project.buildDir}/libs/${project.base.archivesBaseName}-${project.version}.jar"), closureOf<com.matthewprenger.cursegradle.CurseArtifact> {
            relations(closureOf<com.matthewprenger.cursegradle.CurseRelation> {
                if (branch == "mc-1.8") {
                    requiredDependency("legacy-fabric-api")
                } else {
                    requiredDependency("fabric-api")
                    embeddedLibrary("cotton-client-commands")
                    embeddedLibrary("programmerartinjector")
                }
            })
            displayName = "[$branch] ViaFabric ${project.version}"
        })
        afterEvaluate {
            uploadTask.dependsOn("remapJar")
        }
    })
    options(closureOf<com.matthewprenger.cursegradle.Options> {
        forgeGradleIntegration = false
    })
}

minecraft {
    this.intermediaryUrl = JavaFunction {
        "https://dl.bintray.com/legacy-fabric/Legacy-Fabric-Maven/net/fabricmc/intermediary/$it/intermediary-$it-v2.jar"
    }
}

license {
    include("**/*.java")
}
