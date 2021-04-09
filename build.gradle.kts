import org.apache.tools.ant.filters.ReplaceTokens

plugins {
    `java-library`
    id("fabric-loom") version "0.6-SNAPSHOT"
    id("com.palantir.git-version") version "0.12.0-rc2"
    id("com.matthewprenger.cursegradle") version "1.4.0"
    id("maven-publish")
}

group = "com.github.creeper123123321.viafabric"
val gitVersion: groovy.lang.Closure<String> by extra
val versionDetails: groovy.lang.Closure<com.palantir.gradle.gitversion.VersionDetails> by extra

val githubShaInfo: String? = System.getenv("GITHUB_SHA")?.substring(0, 10) // version details doesn't work on gh actions
val branch = try {
    versionDetails().branchName
} catch (e: Exception) {
    "unknown"
}

version = "0.3.1-SNAPSHOT+" + try {
    (githubShaInfo ?: gitVersion()) + "-" + branch
} catch (e: Exception) {
    "unknown"
}
extra.set("archivesBaseName", "ViaFabric")
description = "Client-side and server-side ViaVersion implementation for Fabric"

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
    implementation("us.myles:viaversion:3.3.0-21w14a") { isTransitive = false }
    include("us.myles:viaversion:3.3.0-21w14a")
    implementation("org.yaml:snakeyaml:1.26")
    include("org.yaml:snakeyaml:1.26")

    // Use 1.16 snapshot, probably intermediary will make it work on further versions
    // https://modmuss50.me/fabric.html?&version=1.16
    minecraft("com.mojang:minecraft:21w14a")
    mappings("net.fabricmc:yarn:21w14a+build.10:v2")
    modImplementation("net.fabricmc:fabric-loader:0.11.2")

    modImplementation("net.fabricmc.fabric-api:fabric-api:0.32.7+1.17")
    modImplementation("io.github.prospector:modmenu:1.14.5+build.30")

    //modImplementation("io.github.cottonmc:cotton-client-commands:1.0.1+1.16-rc1")
    //include("io.github.cottonmc:cotton-client-commands:1.0.1+1.16-rc1")
}

if (!System.getenv()["curse_api_key"].isNullOrBlank() && branch.startsWith("mc-")) {
    defaultTasks("clean", "build", "curseforge")
} else {
    defaultTasks("clean", "build")
}

fun cfJavaVersions() : List<String> {
    val ver = mutableListOf("Java 8")
    if (branch != "mc-1.8") {
        ver.add("Java 9")
        ver.add("Java 10")
    }
    return ver
}

fun cfMcVersions() : List<String> {
    return when (branch) {
        "mc-1.8" -> listOf("1.8.9")
        "mc-1.14" -> listOf("1.14.4")
        "mc-1.15" -> listOf("1.15.2")
        "mc-1.16" -> listOf("1.16.4", "1.16.5")
        "mc-1.17" -> listOf("1.17")
        else -> emptyList()
    }
}

fun fabricApiId() : String {
    return if (branch == "mc-1.8") {
        "legacy-fabric-api"
    } else {
        "fabric-api"
    }
}

curseforge {
    apiKey = System.getenv()["curse_api_key"] ?: "undefined"
    project(closureOf<com.matthewprenger.cursegradle.CurseProject> {
        id = "391298"
        changelog = "A changelog can be found at https://github.com/ViaVersion/ViaFabric/commits"
        releaseType = "alpha"

        addGameVersion("Fabric")

        (cfMcVersions() + cfJavaVersions()).forEach {
            addGameVersion(it)
        }
        mainArtifact(file("${project.buildDir}/libs/${project.base.archivesBaseName}-${project.version}.jar"),
                closureOf<com.matthewprenger.cursegradle.CurseArtifact> {
            relations(closureOf<com.matthewprenger.cursegradle.CurseRelation> {
                requiredDependency(fabricApiId())
                if (branch == "mc-1.14" || branch == "mc-1.15") {
                    embeddedLibrary("cotton-client-commands")
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
    accessWidener("src/main/resources/viafabric.accesswidener")
}

tasks.jar {
    from("LICENSE")
}

tasks.withType<JavaCompile> {
    // ensure that the encoding is set to UTF-8, no matter what the system default is
    // this fixes some edge cases with special characters not displaying correctly
    // see http://yodaconditions.net/blog/fix-for-java-file-encoding-problems-with-gradle.html
    // If Javadoc is generated, this must be specified in that task too.
    options.encoding = "UTF-8"
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(8))
    }
    withSourcesJar()
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
