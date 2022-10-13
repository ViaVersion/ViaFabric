dependencies {
    minecraft("com.mojang:minecraft:1.19.2")
    mappings("net.fabricmc:yarn:1.19.2+build.8:v2")

    modImplementation("net.fabricmc.fabric-api:fabric-api:0.60.0+1.19.2")
    modImplementation("com.terraformersmc:modmenu:4.0.6")
}

tasks.compileJava {
    options.release.set(17)
}
