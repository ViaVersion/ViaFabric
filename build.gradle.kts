import com.google.gson.JsonParser
import java.nio.file.Files

plugins {
    id("java")
    id("maven-publish")
    id("org.ajoberstar.grgit") version "5.3.2"
    id("fabric-loom") version "1.11-SNAPSHOT"
    id("legacy-looming") version "1.11-SNAPSHOT" apply false // Version = fabric-loom
    id("com.github.ben-manes.versions") version "0.52.0"
    id("xyz.wagyourtail.jvmdowngrader") version "1.3.3"
    id("me.modmuss50.mod-publish-plugin") version "0.8.4"
}

private val env = System.getenv()
group = "com.viaversion.fabric"
description = "Client-side and server-side ViaVersion implementation for Fabric"
version = "0.4.20+" + env["GITHUB_RUN_NUMBER"] + "-" + getBranch()

fun getBranch(): String {
    val branch = env["GITHUB_REF"] ?: grgit.branch.current().name ?: "unknown"
    return branch.substringAfterLast("/")
}

allprojects {
    apply(plugin = "java")
    apply(plugin = "maven-publish")
    apply(plugin = "fabric-loom")
    apply(plugin = "legacy-looming")

    java {
        toolchain {
            // lwjgl2 works with Adoptium
            languageVersion.set(JavaLanguageVersion.of(21))
            vendor.set(JvmVendorSpec.ADOPTIUM)
        }
        withSourcesJar()
    }

    tasks.withType<JavaCompile>().configureEach {
        options.release.set(8)
    }
    tasks.withType<JavaExec>().configureEach {
        javaLauncher.set(javaToolchains.launcherFor(java.toolchain))
    }

    version = rootProject.version
    group = rootProject.group

    repositories {
        mavenCentral()
        maven("https://repo.viaversion.com/")
        maven("https://maven.fabricmc.net/")
        maven("https://maven.legacyfabric.net/")
        maven("https://maven.terraformersmc.com/releases/")
        maven("https://maven.nucleoid.xyz/")
    }

    dependencies {
        implementation("com.viaversion:viaversion:${rootProject.extra["viaver_version"]}") {
            // transitive = false because Guava is conflicting on runClient
            isTransitive = false
        }
        modImplementation("net.fabricmc:fabric-loader:${rootProject.extra["loader_version"]}")
    }

    tasks.processResources {
        filesMatching("fabric.mod.json") {
            expand(rootProject.properties)
        }
    }
}

subprojects {
    loom {
        runs {
            named("client") {
                client()
                ideConfigGenerated(true)
                runDir("run")
            }
            named("server") {
                server()
                ideConfigGenerated(true)
                runDir("run")
            }
        }
    }

    dependencies {
        implementation(project(":")) {
            exclude(group = "net.fabricmc", module = "fabric-loader") // prevent duplicate fabric-loader on run
        }
    }

    publishing {
        publications {
            create<MavenPublication>("mavenJava") {
                from(components["java"])
            }
        }
    }
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
        }
    }
}

subprojects.forEach {
    rootProject.tasks.named("remapJar").configure {
        dependsOn("${it.path}:remapJar")
    }
}

val includeJ8 = configurations.create("includeJ8")

jvmdg.dg(includeJ8)

dependencies {
    // dummy version
    minecraft("com.mojang:minecraft:1.13.2")
    mappings("net.legacyfabric:yarn:1.13.2+build.541:v2")

    includeJ8("com.viaversion:viaversion:${rootProject.extra["viaver_version"]}")
    includeJ8("com.viaversion:viabackwards:${rootProject.extra["viaback_version"]}")
    includeJ8("com.viaversion:viarewind:${rootProject.extra["viarewind_version"]}")
}

tasks.remapJar.configure {
    nestedJars.from(includeJ8)
    subprojects.forEach { subproject ->
        subproject.tasks.matching { it.name == "remapJar" }.configureEach {
            nestedJars.from(this)
        }
    }
}

tasks.processResources {
    filesMatching("assets/*/lang/*.lang") {
        val langMap = mutableMapOf<String, String>()
        val langDir = rootProject.file("src/main/resources/assets/viafabric/lang").toPath()

        Files.list(langDir)
            .filter { it.toString().endsWith(".json") }
            .forEach { path ->
                val json = JsonParser.parseReader(Files.newBufferedReader(path)).asJsonObject
                val legacyFile = buildString {
                    appendLine()
                    json.entrySet().forEach { entry ->
                        appendLine("${entry.key}=${entry.value.asString}")
                    }
                }
                val langKey = path.fileName.toString().removeSuffix(".json")
                langMap[langKey] = legacyFile
            }

        expand(langMap)
    }
}

val mcReleases = rootProject.extra["publish_mc_versions"].toString().split(",")
        .map { it.trim() }

publishMods {
    file = tasks.remapJar.get().archiveFile
    changelog = "A changelog can be found at https://github.com/ViaVersion/ViaFabric/commits"
    version = rootProject.version.toString()
    displayName = "[${getBranch()}] ViaFabric ${version.get()}"
    modLoaders.add("fabric")
    dryRun = providers.environmentVariable("CURSEFORGE_TOKEN").getOrNull() == null

    curseforge {
        accessToken = providers.environmentVariable("CURSEFORGE_TOKEN").orNull
        projectId = "391298"
        type = BETA // alpha is hidden by default

        javaVersions.addAll(
                (8..22).map { JavaVersion.toVersion(it) }
        )
        minecraftVersions.addAll(mcReleases)
        optional("legacy-fabric-api")
    }
    modrinth {
        accessToken = providers.environmentVariable("MODRINTH_TOKEN").orNull
        projectId = "YlKdE5VK"
        type = ALPHA
        minecraftVersions.addAll(mcReleases)
        optional("legacy-fabric-api")
        embeds("viaversion")
    }
}

defaultTasks("clean", "build")
