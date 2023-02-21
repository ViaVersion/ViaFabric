dependencies {
    minecraft("com.mojang:minecraft:23w07a")
    mappings("net.fabricmc:yarn:23w07a+build.10:v2")

    modImplementation("net.fabricmc.fabric-api:fabric-api:0.74.1+1.19.4")
    modImplementation("com.terraformersmc:modmenu:5.0.0-alpha.4")
}

tasks.compileJava {
    options.release.set(17)
}
