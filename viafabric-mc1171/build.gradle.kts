dependencies {
    minecraft("com.mojang:minecraft:1.17.1")
    mappings("net.fabricmc:yarn:1.17.1+build.65:v2")

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