dependencies {
    minecraft("com.mojang:minecraft:1.20.4")
    mappings("net.fabricmc:yarn:1.20.4+build.3:v2")

    modImplementation(fabricApi.module("fabric-api-base", "0.97.2+1.20.4"))
    modImplementation(fabricApi.module("fabric-resource-loader-v0", "0.97.2+1.20.4"))
    modImplementation(fabricApi.module("fabric-command-api-v1", "0.97.2+1.20.4"))
    modImplementation(fabricApi.module("fabric-lifecycle-events-v1", "0.97.2+1.20.4"))
    modImplementation(fabricApi.module("fabric-screen-api-v1", "0.97.2+1.20.4"))
    modImplementation(fabricApi.module("fabric-registry-sync-v0", "0.97.2+1.20.4"))
    modImplementation("com.terraformersmc:modmenu:9.2.0")
}

tasks.compileJava {
    options.release.set(17)
}
