dependencies {
    minecraft("com.mojang:minecraft:1.20.1")
    mappings(loom.layered {
        officialMojangMappings()
        parchment("org.parchmentmc.data:parchment-1.20.1:2023.09.03@zip")
    })

    modImplementation(fabricApi.module("fabric-api-base", "0.92.2+1.20.1"))
    modImplementation(fabricApi.module("fabric-resource-loader-v0", "0.92.2+1.20.1"))
    modImplementation(fabricApi.module("fabric-command-api-v1", "0.92.2+1.20.1"))
    modImplementation(fabricApi.module("fabric-lifecycle-events-v1", "0.92.2+1.20.1"))
    modImplementation(fabricApi.module("fabric-screen-api-v1", "0.92.2+1.20.1"))
    modImplementation("com.terraformersmc:modmenu:7.2.2")
}

tasks.compileJava {
    options.release.set(17)
}
