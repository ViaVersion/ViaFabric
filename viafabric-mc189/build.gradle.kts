dependencies {
    minecraft("com.mojang:minecraft:1.8.9")
    mappings("net.legacyfabric:yarn:1.8.9+build.541:v2")

    modImplementation("net.legacyfabric.legacy-fabric-api:legacy-fabric-api:1.9.4+1.8.9")

    // fix newer java
    @Suppress("GradlePackageUpdate", "RedundantSuppression")
    implementation("io.netty:netty-all:4.0.56.Final")
}
