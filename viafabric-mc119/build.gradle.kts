dependencies {
    minecraft("com.mojang:minecraft:1.19.4")
    mappings("net.fabricmc:yarn:1.19.4+build.2:v2")

    modImplementation("net.fabricmc.fabric-api:fabric-api:0.83.0+1.19.4")
    modImplementation("com.terraformersmc:modmenu:6.2.3")
}

tasks.compileJava {
    options.release.set(17)
}
