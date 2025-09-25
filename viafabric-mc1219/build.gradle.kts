dependencies {
    minecraft("com.mojang:minecraft:1.21.9-rc1")
    mappings(loom.officialMojangMappings())

    modImplementation(fabricApi.module("fabric-api-base", "0.133.12+1.21.9"))
    modImplementation(fabricApi.module("fabric-resource-loader-v0", "0.133.12+1.21.9"))
    modImplementation(fabricApi.module("fabric-command-api-v2", "0.133.12+1.21.9"))
    modImplementation(fabricApi.module("fabric-lifecycle-events-v1", "0.133.12+1.21.9"))
    modImplementation(fabricApi.module("fabric-screen-api-v1", "0.133.12+1.21.9"))
    modImplementation(fabricApi.module("fabric-registry-sync-v0", "0.133.12+1.21.9"))
    modImplementation(fabricApi.module("fabric-key-binding-api-v1", "0.133.12+1.21.9"))
    modCompileOnly("com.terraformersmc:modmenu:15.0.0")
}

tasks.compileJava {
    options.release.set(21)
}
