dependencies {
    minecraft("com.mojang:minecraft:1.21.3")
    mappings("net.fabricmc:yarn:1.21.3+build.2:v2")

    modImplementation("net.fabricmc.fabric-api:fabric-api:0.107.0+1.21.3")
    modImplementation("com.terraformersmc:modmenu:12.0.0-beta.1")
}

tasks.compileJava {
    options.release.set(21)
}
