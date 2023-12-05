dependencies {
    minecraft("com.mojang:minecraft:1.20.3")
    mappings("net.fabricmc:yarn:1.20.3+build.1:v2")

    modImplementation("net.fabricmc.fabric-api:fabric-api:0.91.1+1.20.3")
    modImplementation("com.terraformersmc:modmenu:8.0.0")
}

tasks.compileJava {
    options.release.set(17)
}
