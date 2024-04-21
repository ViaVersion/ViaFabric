dependencies {
    minecraft("com.mojang:minecraft:1.17.1")
    mappings("net.fabricmc:yarn:1.17.1+build.65:v2")

    modImplementation("net.fabricmc.fabric-api:fabric-api:0.46.1+1.17")
    modImplementation("com.terraformersmc:modmenu:2.0.17")
}

tasks.compileJava {
    options.release.set(16)
}