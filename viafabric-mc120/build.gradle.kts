dependencies {
    minecraft("com.mojang:minecraft:1.20.2")
    mappings("net.fabricmc:yarn:1.20.2+build.2:v2")

    modImplementation("net.fabricmc.fabric-api:fabric-api:0.89.3+1.20.2")
    modImplementation("com.terraformersmc:modmenu:8.0.0")
}

tasks.compileJava {
    options.release.set(17)
}
