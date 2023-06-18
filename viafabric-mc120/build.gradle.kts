dependencies {
    minecraft("com.mojang:minecraft:1.20.1")
    mappings("net.fabricmc:yarn:1.20.1+build.2:v2")

    modImplementation("net.fabricmc.fabric-api:fabric-api:0.83.1+1.20.1")
    modImplementation("com.terraformersmc:modmenu:7.1.0")
}

tasks.compileJava {
    options.release.set(17)
}
