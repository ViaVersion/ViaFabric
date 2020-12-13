import org.apache.tools.ant.filters.ReplaceTokens
import java.util.function.Function as JavaFunction

plugins {
    id("java")
    id("net.minecrell.licenser") version "0.4.1"
    id("fabric-loom") version "0.5-SNAPSHOT"
    id("com.palantir.git-version") version "0.12.0-rc2"
    id("com.matthewprenger.cursegradle") version "1.4.0"
    id("maven-publish")
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

version = "0.2.16-SNAPSHOT+" + try {
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
    maven(url = "https://jitpack.io/")
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
    implementation("us.myles:viaversion:3.2.1") { isTransitive = false }
    include("us.myles:viaversion:3.2.1")
    implementation("org.yaml:snakeyaml:1.26")
    include("org.yaml:snakeyaml:1.26")

    // Use 1.8.9 Legacy Fabric https://github.com/Legacy-Fabric/fabric-example-mod/blob/master/gradle.properties
    implementation("com.google.guava:guava:23.5-jre")
    minecraft("com.mojang:minecraft:1.8.9")
    mappings("net.fabricmc:yarn:1.8.9+build.202008241659:v2")
    modCompile("net.fabricmc:fabric-loader-1.8.9:0.8.7+build.202008300717") {
        exclude(module = "guava")
    }

    modImplementation("net.fabricmc.fabric-api:fabric-api:0.7.3")
    modImplementation("com.github.shedaniel:modmenu-1.8.9:1.8.9-SNAPSHOT")

    //modImplementation("io.github.cottonmc:cotton-client-commands:1.0.1+1.16-rc1")
    //include("io.github.cottonmc:cotton-client-commands:1.0.1+1.16-rc1")
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
            "mc-1.14" -> {
                addGameVersion("1.14")
                addGameVersion("1.14.1")
                addGameVersion("1.14.2")
                addGameVersion("1.14.3")
                addGameVersion("1.14.4")
            }
            "mc-1.15" -> {
                addGameVersion("1.15")
                addGameVersion("1.15.1")
                addGameVersion("1.15.2")
            }
            "mc-1.16" -> {
                addGameVersion("1.16")
                addGameVersion("1.16.1")
                addGameVersion("1.16.2")
                addGameVersion("1.16.3")
                addGameVersion("1.16.4")
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
    this.intermediaryUrl = JavaFunction {
        "https://dl.bintray.com/legacy-fabric/Legacy-Fabric-Maven/net/fabricmc/intermediary/$it/intermediary-$it-v2.jar"
    }
    accessWidener("src/main/resources/viafabric.accesswidener")
}

license {
    include("**/*.java")
}

tasks.jar {
    from("LICENSE")
}


// Loom will automatically attach sourcesJar to a RemapSourcesJar task and to the "build" task
// if it is present.
// If you remove this task, sources will not be generated.
tasks.register("sourcesJar", type = Jar::class) {
    dependsOn(tasks.getByName("classes"))
    archiveClassifier.set("sources")
    from(sourceSets.main.get().allSource)
}

// configure the maven publication
publishing {
    publications {
        create<MavenPublication>("maven") {
            // add all the jars that should be included when publishing to maven
            artifact(tasks.getByName("remapJar")) {
                builtBy(tasks.getByName("remapJar"))
            }
            artifact(tasks.getByName("sourcesJar")) {
                builtBy(tasks.getByName("remapSourcesJar"))
            }
        }
    }

    // select the repositories you want to publish to
    repositories {
        // uncomment to publish to the local maven
        // mavenLocal()
    }
}
