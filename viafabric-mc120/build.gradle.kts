dependencies {
    minecraft("com.mojang:minecraft:1.20.2")
    mappings("net.fabricmc:yarn:1.20.2+build.1:v2")

    modImplementation("net.fabricmc.fabric-api:fabric-api:0.89.0+1.20.2")
    modImplementation("com.terraformersmc:modmenu:8.0.0-beta.2")
}

tasks.compileJava {
    options.release.set(17)
}
