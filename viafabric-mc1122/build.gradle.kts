dependencies {
    minecraft("com.mojang:minecraft:1.12.2")
    mappings("net.legacyfabric:yarn:1.12.2+build.541:v2")

    modImplementation("net.legacyfabric.legacy-fabric-api:legacy-fabric-api:1.10.2+1.12.2")

    // fix newer java
    @Suppress("GradlePackageUpdate", "RedundantSuppression")
    implementation("io.netty:netty-all:4.0.56.Final")
}
