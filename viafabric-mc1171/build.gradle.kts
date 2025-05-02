dependencies {
    minecraft("com.mojang:minecraft:1.17.1")
    mappings(loom.layered {
        officialMojangMappings()
        parchment("org.parchmentmc.data:parchment-1.17.1:2021.12.12@zip")
    })

    modImplementation(fabricApi.module("fabric-api-base", "0.46.1+1.17"))
    modImplementation(fabricApi.module("fabric-resource-loader-v0", "0.46.1+1.17"))
    modImplementation(fabricApi.module("fabric-command-api-v1", "0.46.1+1.17"))
    modImplementation(fabricApi.module("fabric-lifecycle-events-v1", "0.46.1+1.17"))
    modImplementation(fabricApi.module("fabric-screen-api-v1", "0.46.1+1.17"))
    modImplementation("com.terraformersmc:modmenu:2.0.17")
}

tasks.compileJava {
    options.release.set(16)
}
