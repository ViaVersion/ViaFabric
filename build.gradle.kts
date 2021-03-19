import org.apache.tools.ant.filters.ReplaceTokens

plugins {
    id("java")
    id("net.minecrell.licenser") version "0.4.1"
    id("fabric-loom") version "0.6-SNAPSHOT"
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

version = "0.3.1-SNAPSHOT+" + try {
    gitVersion() + "-" + branch
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
    implementation("us.myles:viaversion:3.2.1") { isTransitive = false }
    include("us.myles:viaversion:3.2.1")
    implementation("org.yaml:snakeyaml:1.26")
    include("org.yaml:snakeyaml:1.26")

    // Use 1.16 snapshot, probably intermediary will make it work on further versions
    // https://modmuss50.me/fabric.html?&version=1.16
    minecraft("com.mojang:minecraft:1.16.3")
    mappings("net.fabricmc:yarn:1.16.3+build.5:v2")
    modImplementation("net.fabricmc:fabric-loader:0.9.3+build.207")

    modImplementation("net.fabricmc.fabric-api:fabric-api:0.20.2+build.402-1.16")
    modImplementation("io.github.prospector:modmenu:1.14.5+build.30")

    modImplementation("io.github.cottonmc:cotton-client-commands:1.0.1+1.16-rc1")
    include("io.github.cottonmc:cotton-client-commands:1.0.1+1.16-rc1")
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
            "mc-1.8" -> listOf("1.8.9")
            "mc-1.14" -> listOf("1.14", "1.14.1", "1.14.2", "1.14.3", "1.14.4")
            "mc-1.15" -> listOf("1.15", "1.15.1", "1.15.2")
            "mc-1.16" -> listOf("1.16", "1.16.1", "1.16.2", "1.16.3", "1.16.4", "1.16.5")
            "mc-1.17" -> listOf("1.17")
            else -> emptyList()
        }.forEach {
            addGameVersion(it)
        }
        addGameVersion("Fabric")
        mainArtifact(file("${project.buildDir}/libs/${project.base.archivesBaseName}-${project.version}.jar"), closureOf<com.matthewprenger.cursegradle.CurseArtifact> {
            relations(closureOf<com.matthewprenger.cursegradle.CurseRelation> {
                if (branch == "mc-1.8") {
                    requiredDependency("legacy-fabric-api")
                } else {
                    requiredDependency("fabric-api")
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

license {
    include("**/*.java")
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

    // The Minecraft launcher currently installs Java 8 for users, so your mod probably wants to target Java 8 too
    // JDK 9 introduced a new way of specifying this that will make sure no newer classes or methods are used.
    // We'll use that if it's available, but otherwise we'll use the older option.
    val targetVersion = 8
    if (JavaVersion.current().isJava9Compatible) {
        options.release.set(targetVersion)
    } else {
        sourceCompatibility = JavaVersion.toVersion(targetVersion).toString()
        targetCompatibility = JavaVersion.toVersion(targetVersion).toString()
    }
}

java {
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
