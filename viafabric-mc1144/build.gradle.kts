dependencies {
    minecraft("com.mojang:minecraft:1.14.4")
    mappings(loom.officialMojangMappings())

    modImplementation(fabricApi.module("fabric-api-base", "0.28.5+1.14"))
    modImplementation(fabricApi.module("fabric-resource-loader-v0", "0.28.5+1.14"))
    modImplementation(fabricApi.module("fabric-command-api-v1", "0.28.5+1.14"))
    modImplementation(fabricApi.module("fabric-lifecycle-events-v1", "0.28.5+1.14"))
    modImplementation("io.github.prospector:modmenu:1.7.17+build.1")
    modImplementation("io.github.cottonmc:cotton-client-commands:1.1.0+1.15.2")
}
