dependencies {
    minecraft("com.mojang:minecraft:1.21.5-pre3")
    mappings("net.fabricmc:yarn:1.21.5-pre3+build.2:v2")

    modImplementation(fabricApi.module("fabric-api-base", "0.119.2+1.21.5"))
    modImplementation(fabricApi.module("fabric-resource-loader-v0", "0.119.2+1.21.5"))
    modImplementation(fabricApi.module("fabric-command-api-v1", "0.119.2+1.21.5"))
    modImplementation(fabricApi.module("fabric-lifecycle-events-v1", "0.119.2+1.21.5"))
    modImplementation(fabricApi.module("fabric-screen-api-v1", "0.119.2+1.21.5"))
    modImplementation(fabricApi.module("fabric-registry-sync-v0", "0.119.2+1.21.5"))
    modImplementation(fabricApi.module("fabric-key-binding-api-v1", "0.119.2+1.21.5"))
    modImplementation("com.terraformersmc:modmenu:13.0.0")
}

tasks.compileJava {
    options.release.set(21)
}
