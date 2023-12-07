dependencies {
    minecraft("com.mojang:minecraft:1.20.4")
    mappings("net.fabricmc:yarn:1.20.4+build.1:v2")

    modImplementation("net.fabricmc.fabric-api:fabric-api:0.91.1+1.20.4")
    modImplementation("com.terraformersmc:modmenu:9.0.0-pre.1")
}

tasks.compileJava {
    options.release.set(17)
}
