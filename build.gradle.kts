import org.apache.tools.ant.filters.ReplaceTokens

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

version = "0.2.10-SNAPSHOT+" + try {
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
    maven(url = "https://maven.extracraftx.com")
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
    implementation("us.myles:viaversion:3.1.0") { isTransitive = false }
    include("us.myles:viaversion:3.1.0")
    include("org.yaml:snakeyaml:1.26")

    // Use 1.14.4 release, probably intermediary will make it work on snapshots
    // https://modmuss50.me/fabric.html?&version=1.14.4
    minecraft("com.mojang:minecraft:1.14.4")
    mappings("net.fabricmc:yarn:1.14.4+build.16:v2")
    modImplementation("net.fabricmc:fabric-loader:0.8.2+build.194")

    modImplementation("net.fabricmc.fabric-api:fabric-api:0.13.1+build.257-1.14")

    modImplementation("io.github.cottonmc:cotton-client-commands:1.0.0+1.15.2")
    include("io.github.cottonmc:cotton-client-commands:1.0.0+1.15.2")
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
        addGameVersion("Java 8")
        if (branch != "mc-1.8") {
            addGameVersion("Java 9")
            addGameVersion("Java 10")
        }
        when (branch) {
            "mc-1.8" -> addGameVersion("1.8.9")
            "mc-1.14-1.15" -> {
                addGameVersion("1.14")
                addGameVersion("1.14.1")
                addGameVersion("1.14.2")
                addGameVersion("1.14.3")
                addGameVersion("1.14.4")
                addGameVersion("1.15")
                addGameVersion("1.15.1")
                addGameVersion("1.15.2")
            }
            "mc-1.16" -> {
                addGameVersion("1.16-Snapshot")
                addGameVersion("1.16")
                addGameVersion("1.16.1")
                addGameVersion("1.16.2")
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
//                    embeddedLibrary("programmerartinjector")
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
}

license {
    include("**/*.java")
}
