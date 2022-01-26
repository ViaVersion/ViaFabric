dependencies {
    minecraft("com.mojang:minecraft:1.8.9")
    mappings("net.fabricmc:yarn:1.8.9+build.202112162000:v2")

    modImplementation("net.legacyfabric.legacy-fabric-api:legacy-fabric-api:1.2.2+1.8.9") {
        exclude(module = "fabric-loader-1.8.9")
    }
    modImplementation("io.github.boogiemonster1o1:rewoven-modmenu:1.0.0+1.8.9") {
        isTransitive = false
    }
}

loom {
    intermediaryUrl.set("https://maven.legacyfabric.net/net/fabricmc/intermediary/%1\$s/intermediary-%1\$s-v2.jar")
}
