dependencies {
    minecraft("com.mojang:minecraft:1.21.4")
    mappings("net.fabricmc:yarn:1.21.4+build.1:v2")

    modImplementation(fabricApi.module("fabric-api-base", "0.110.5+1.21.4"))
    modImplementation(fabricApi.module("fabric-resource-loader-v0", "0.110.5+1.21.4"))
    modImplementation(fabricApi.module("fabric-command-api-v1", "0.110.5+1.21.4"))
    modImplementation(fabricApi.module("fabric-lifecycle-events-v1", "0.110.5+1.21.4"))
    modImplementation(fabricApi.module("fabric-screen-api-v1", "0.110.5+1.21.4"))
    modImplementation(fabricApi.module("fabric-registry-sync-v0", "0.110.5+1.21.4"))
    modImplementation(fabricApi.module("fabric-key-binding-api-v1", "0.110.5+1.21.4"))
    modImplementation("com.terraformersmc:modmenu:13.0.0-beta.1")
}

tasks.compileJava {
    options.release.set(21)
}
