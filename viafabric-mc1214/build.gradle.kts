dependencies {
    minecraft("com.mojang:minecraft:1.21.4")
    mappings("net.fabricmc:yarn:1.21.4+build.1:v2")

    modImplementation("net.fabricmc.fabric-api:fabric-api:0.110.5+1.21.4")
    modImplementation("com.terraformersmc:modmenu:13.0.0-beta.1")
}

tasks.compileJava {
    options.release.set(21)
}
