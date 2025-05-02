dependencies {
    minecraft("com.mojang:minecraft:1.20.6")
    mappings(loom.layered {
        officialMojangMappings()
        parchment("org.parchmentmc.data:parchment-1.20.6:2024.06.16@zip")
    })

    modImplementation(fabricApi.module("fabric-api-base", "0.100.8+1.20.6"))
    modImplementation(fabricApi.module("fabric-resource-loader-v0", "0.100.8+1.20.6"))
    modImplementation(fabricApi.module("fabric-command-api-v1", "0.100.8+1.20.6"))
    modImplementation(fabricApi.module("fabric-lifecycle-events-v1", "0.100.8+1.20.6"))
    modImplementation(fabricApi.module("fabric-screen-api-v1", "0.100.8+1.20.6"))
    modImplementation(fabricApi.module("fabric-registry-sync-v0", "0.100.8+1.20.6"))
    modImplementation(fabricApi.module("fabric-key-binding-api-v1", "0.100.8+1.20.6"))
    modImplementation("com.terraformersmc:modmenu:10.0.0")
}

tasks.compileJava {
    options.release.set(21)
}
