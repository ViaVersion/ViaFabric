dependencies {
    minecraft("com.mojang:minecraft:1.19.4-rc2")
    mappings("net.fabricmc:yarn:1.19.4-rc2+build.1:v2")

    modImplementation("net.fabricmc.fabric-api:fabric-api:0.75.3+1.19.4")
    modImplementation("com.terraformersmc:modmenu:5.0.0-alpha.4")
}

tasks.compileJava {
    options.release.set(17)
}
