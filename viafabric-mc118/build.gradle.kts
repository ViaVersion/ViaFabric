dependencies {
    minecraft("com.mojang:minecraft:1.18.2")
    mappings("net.fabricmc:yarn:1.18.2+build.4:v2")

    modImplementation("net.fabricmc.fabric-api:fabric-api:0.77.0+1.18.2")
    modImplementation("com.terraformersmc:modmenu:3.2.5")
}

tasks.compileJava {
    options.release.set(17)
}