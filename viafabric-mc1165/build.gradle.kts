dependencies {
    minecraft("com.mojang:minecraft:1.16.5")
    mappings(loom.layered {
        officialMojangMappings()
        parchment("org.parchmentmc.data:parchment-1.16.5:2022.03.06@zip")
    })

    modImplementation(fabricApi.module("fabric-api-base", "0.42.0+1.16"))
    modImplementation(fabricApi.module("fabric-resource-loader-v0", "0.42.0+1.16"))
    modImplementation(fabricApi.module("fabric-command-api-v1", "0.42.0+1.16"))
    modImplementation(fabricApi.module("fabric-lifecycle-events-v1", "0.42.0+1.16"))
    modImplementation("com.terraformersmc:modmenu:1.16.23")
}
