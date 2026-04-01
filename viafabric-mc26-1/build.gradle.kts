dependencies {
    minecraft("com.mojang:minecraft:26.1.1")

    implementation(fabricApi.module("fabric-api-base", "0.145.2+26.1.1"))
    implementation(fabricApi.module("fabric-resource-loader-v0", "0.145.2+26.1.1"))
    implementation(fabricApi.module("fabric-command-api-v2", "0.145.2+26.1.1"))
    implementation(fabricApi.module("fabric-lifecycle-events-v1", "0.145.2+26.1.1"))
    implementation(fabricApi.module("fabric-screen-api-v1", "0.145.2+26.1.1"))
    implementation(fabricApi.module("fabric-registry-sync-v0", "0.145.2+26.1.1"))
    compileOnly("com.terraformersmc:modmenu:18.0.0-alpha.8")
}
