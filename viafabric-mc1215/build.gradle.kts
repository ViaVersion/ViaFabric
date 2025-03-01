dependencies {
    minecraft("com.mojang:minecraft:25w09b")
    mappings("net.fabricmc:yarn:25w09b+build.5:v2")

    modImplementation(fabricApi.module("fabric-api-base", "0.118.2+1.21.5"))
    modImplementation(fabricApi.module("fabric-resource-loader-v0", "0.118.2+1.21.5"))
    modImplementation(fabricApi.module("fabric-command-api-v1", "0.118.2+1.21.5"))
    modImplementation(fabricApi.module("fabric-lifecycle-events-v1", "0.118.2+1.21.5"))
    modImplementation(fabricApi.module("fabric-screen-api-v1", "0.118.2+1.21.5"))
    modImplementation(fabricApi.module("fabric-registry-sync-v0", "0.118.2+1.21.5"))
    modImplementation(fabricApi.module("fabric-key-binding-api-v1", "0.118.2+1.21.5"))
    modImplementation("com.terraformersmc:modmenu:13.0.0")
}

tasks.compileJava {
    options.release.set(21)
}
