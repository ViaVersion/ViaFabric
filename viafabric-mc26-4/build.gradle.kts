dependencies {
    minecraft("com.mojang:minecraft:26.4-snapshot-2")

    implementation(fabricApi.module("fabric-api-base", "0.162.1+26.4"))
    implementation(fabricApi.module("fabric-resource-loader-v0", "0.162.1+26.4"))
    implementation(fabricApi.module("fabric-command-api-v2", "0.162.1+26.4"))
    implementation(fabricApi.module("fabric-lifecycle-events-v1", "0.162.1+26.4"))
    implementation(fabricApi.module("fabric-screen-api-v1", "0.162.1+26.4"))
    implementation(fabricApi.module("fabric-registry-sync-v0", "0.162.1+26.4"))
    compileOnly("com.terraformersmc:modmenu:21.0.0-beta.1")
}
