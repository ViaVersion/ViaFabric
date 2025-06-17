dependencies {
    minecraft("com.mojang:minecraft:1.21.6")
    mappings(loom.officialMojangMappings())

    modImplementation(fabricApi.module("fabric-api-base", "0.127.0+1.21.6"))
    modImplementation(fabricApi.module("fabric-resource-loader-v0", "0.127.0+1.21.6"))
    modImplementation(fabricApi.module("fabric-command-api-v2", "0.127.0+1.21.6"))
    modImplementation(fabricApi.module("fabric-lifecycle-events-v1", "0.127.0+1.21.6"))
    modImplementation(fabricApi.module("fabric-screen-api-v1", "0.127.0+1.21.6"))
    modImplementation(fabricApi.module("fabric-registry-sync-v0", "0.127.0+1.21.6"))
    modImplementation(fabricApi.module("fabric-key-binding-api-v1", "0.127.0+1.21.6"))
    modCompileOnly("com.terraformersmc:modmenu:15.0.0-beta.1")
}

tasks.compileJava {
    options.release.set(21)
}
