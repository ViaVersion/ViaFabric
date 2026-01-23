dependencies {
    minecraft("com.mojang:minecraft:26.1-snapshot-4")

    implementation(fabricApi.module("fabric-api-base", "0.142.1+26.1"))
    implementation(fabricApi.module("fabric-resource-loader-v0", "0.142.1+26.1"))
    implementation(fabricApi.module("fabric-command-api-v2", "0.142.1+26.1"))
    implementation(fabricApi.module("fabric-lifecycle-events-v1", "0.142.1+26.1"))
    implementation(fabricApi.module("fabric-screen-api-v1", "0.142.1+26.1"))
    implementation(fabricApi.module("fabric-registry-sync-v0", "0.142.1+26.1"))
    compileOnly("com.terraformersmc:modmenu:16.0.0-rc.1")
}
