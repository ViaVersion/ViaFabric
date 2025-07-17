dependencies {
    minecraft("com.mojang:minecraft:1.21.8")
    mappings(loom.officialMojangMappings())

    modImplementation(fabricApi.module("fabric-api-base", "0.129.0+1.21.8"))
    modImplementation(fabricApi.module("fabric-resource-loader-v0", "0.129.0+1.21.8"))
    modImplementation(fabricApi.module("fabric-command-api-v2", "0.129.0+1.21.8"))
    modImplementation(fabricApi.module("fabric-lifecycle-events-v1", "0.129.0+1.21.8"))
    modImplementation(fabricApi.module("fabric-screen-api-v1", "0.129.0+1.21.8"))
    modImplementation(fabricApi.module("fabric-registry-sync-v0", "0.129.0+1.21.8"))
    modImplementation(fabricApi.module("fabric-key-binding-api-v1", "0.129.0+1.21.8"))
    modCompileOnly("com.terraformersmc:modmenu:15.0.0-beta.3")
}

tasks.compileJava {
    options.release.set(21)
}
