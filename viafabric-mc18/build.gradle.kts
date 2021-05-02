import java.util.function.Function as JFun
version = rootProject.version

dependencies {
	minecraft("com.mojang:minecraft:1.8.9")
	mappings("net.fabricmc:yarn:1.8.9+build.202103291533:v2")
	modCompile("net.fabricmc:fabric-loader:0.10.5+build.213")

	modImplementation("net.legacyfabric.legacy-fabric-api:legacy-fabric-api:1.1.0+1.8.9")
	modImplementation("io.github.boogiemonster1o1:modmenu:0.1.0+1.8.9")
}

minecraft {
	intermediaryUrl = JFun {
		"https://maven.legacyfabric.net/net/fabricmc/intermediary/$it/intermediary-$it-v2.jar"
	}
}