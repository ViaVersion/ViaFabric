dependencies {
    minecraft("com.mojang:minecraft:26.3-pre-3")

    implementation(fabricApi.module("fabric-api-base", "0.160.1+26.3"))
    implementation(fabricApi.module("fabric-resource-loader-v0", "0.160.1+26.3"))
    implementation(fabricApi.module("fabric-command-api-v2", "0.160.1+26.3"))
    implementation(fabricApi.module("fabric-lifecycle-events-v1", "0.160.1+26.3"))
    implementation(fabricApi.module("fabric-screen-api-v1", "0.160.1+26.3"))
    implementation(fabricApi.module("fabric-registry-sync-v0", "0.160.1+26.3"))
    compileOnly("com.terraformersmc:modmenu:21.0.0-alpha.1")
}
