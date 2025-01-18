dependencies {
    minecraft("com.mojang:minecraft:1.19.4")
    mappings("net.fabricmc:yarn:1.19.4+build.2:v2")

    modImplementation(fabricApi.module("fabric-api-base", "0.87.2+1.19.4"))
    modImplementation(fabricApi.module("fabric-resource-loader-v0", "0.87.2+1.19.4"))
    modImplementation(fabricApi.module("fabric-command-api-v1", "0.87.2+1.19.4"))
    modImplementation(fabricApi.module("fabric-lifecycle-events-v1", "0.87.2+1.19.4"))
    modImplementation(fabricApi.module("fabric-screen-api-v1", "0.87.2+1.19.4"))
    modImplementation("com.terraformersmc:modmenu:6.3.1")
}

tasks.compileJava {
    options.release.set(17)
}
