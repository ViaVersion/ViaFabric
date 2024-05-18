dependencies {
    minecraft("com.mojang:minecraft:1.20.1")
    mappings("net.fabricmc:yarn:1.20.1+build.10:v2")

    modImplementation("net.fabricmc.fabric-api:fabric-api:0.92.1+1.20.1")
    modImplementation("com.terraformersmc:modmenu:7.2.2")
}

tasks.compileJava {
    options.release.set(17)
}