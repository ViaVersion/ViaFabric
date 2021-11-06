dependencies {
	minecraft("com.mojang:minecraft:1.8.9")
	mappings("net.fabricmc:yarn:1.8.9+build.202107080308:v2")

	modImplementation("net.legacyfabric.legacy-fabric-api:legacy-fabric-api:1.1.0+1.8.9")
	modImplementation("io.github.boogiemonster1o1:modmenu:0.1.0+1.8.9")
}

loom {
	intermediaryUrl.set("https://maven.legacyfabric.net/net/fabricmc/intermediary/%1\$s/intermediary-%1\$s-v2.jar")
}