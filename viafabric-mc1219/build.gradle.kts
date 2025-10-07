dependencies {
    minecraft("com.mojang:minecraft:1.21.10")
    mappings(loom.officialMojangMappings())

    modImplementation(fabricApi.module("fabric-api-base", "0.134.1+1.21.10"))
    modImplementation(fabricApi.module("fabric-resource-loader-v0", "0.134.1+1.21.10"))
    modImplementation(fabricApi.module("fabric-command-api-v2", "0.134.1+1.21.10"))
    modImplementation(fabricApi.module("fabric-lifecycle-events-v1", "0.134.1+1.21.10"))
    modImplementation(fabricApi.module("fabric-screen-api-v1", "0.134.1+1.21.10"))
    modImplementation(fabricApi.module("fabric-registry-sync-v0", "0.134.1+1.21.10"))
    modImplementation(fabricApi.module("fabric-key-binding-api-v1", "0.134.1+1.21.10"))
    modCompileOnly("com.terraformersmc:modmenu:16.0.0-rc.1")
}

tasks.compileJava {
    options.release.set(21)
}
