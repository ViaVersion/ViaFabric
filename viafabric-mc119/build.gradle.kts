dependencies {
    minecraft("com.mojang:minecraft:1.19.3")
    mappings("net.fabricmc:yarn:1.19.3+build.2:v2")

    modImplementation("net.fabricmc.fabric-api:fabric-api:0.68.1+1.19.3")
    modImplementation("com.terraformersmc:modmenu:5.0.0-alpha.4")
}

tasks.compileJava {
    options.release.set(17)
}
