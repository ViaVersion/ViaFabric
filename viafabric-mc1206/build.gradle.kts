dependencies {
    minecraft("com.mojang:minecraft:1.20.6")
    mappings("net.fabricmc:yarn:1.20.6+build.3:v2")

    modImplementation("net.fabricmc.fabric-api:fabric-api:0.100.0+1.20.6")
    modImplementation("com.terraformersmc:modmenu:10.0.0-beta.1")
}

tasks.compileJava {
    options.release.set(21)
}
