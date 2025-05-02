dependencies {
    minecraft("com.mojang:minecraft:1.21.5")
    mappings(loom.layered {
        officialMojangMappings()
        parchment("org.parchmentmc.data:parchment-1.21.5:2025.04.19@zip")
    })

    modImplementation(fabricApi.module("fabric-api-base", "0.119.5+1.21.5"))
    modImplementation(fabricApi.module("fabric-resource-loader-v0", "0.119.5+1.21.5"))
    modImplementation(fabricApi.module("fabric-command-api-v1", "0.119.5+1.21.5"))
    modImplementation(fabricApi.module("fabric-lifecycle-events-v1", "0.119.5+1.21.5"))
    modImplementation(fabricApi.module("fabric-screen-api-v1", "0.119.5+1.21.5"))
    modImplementation(fabricApi.module("fabric-registry-sync-v0", "0.119.5+1.21.5"))
    modImplementation(fabricApi.module("fabric-key-binding-api-v1", "0.119.5+1.21.5"))
    modImplementation("com.terraformersmc:modmenu:14.0.0-rc.2")
}

tasks.compileJava {
    options.release.set(21)
}
