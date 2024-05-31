dependencies {
    minecraft("com.mojang:minecraft:1.20.4")
    mappings("net.fabricmc:yarn:1.20.4+build.3:v2")

    modImplementation("net.fabricmc.fabric-api:fabric-api:0.97.1+1.20.4")
    modImplementation("com.terraformersmc:modmenu:9.0.0")
}

tasks.compileJava {
    options.release.set(17)
}
