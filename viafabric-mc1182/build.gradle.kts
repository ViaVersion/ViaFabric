dependencies {
    minecraft("com.mojang:minecraft:1.18.2")
    mappings(loom.layered {
        officialMojangMappings()
        parchment("org.parchmentmc.data:parchment-1.18.2:2022.11.06@zip")
    })

    modImplementation(fabricApi.module("fabric-api-base", "0.77.0+1.18.2"))
    modImplementation(fabricApi.module("fabric-resource-loader-v0", "0.77.0+1.18.2"))
    modImplementation(fabricApi.module("fabric-command-api-v1", "0.77.0+1.18.2"))
    modImplementation(fabricApi.module("fabric-lifecycle-events-v1", "0.77.0+1.18.2"))
    modImplementation(fabricApi.module("fabric-screen-api-v1", "0.77.0+1.18.2"))
    modImplementation("com.terraformersmc:modmenu:3.2.5")
}

tasks.compileJava {
    options.release.set(17)
}
