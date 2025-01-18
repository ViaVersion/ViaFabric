dependencies {
    minecraft("com.mojang:minecraft:1.18.2")
    mappings("net.fabricmc:yarn:1.18.2+build.4:v2")

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