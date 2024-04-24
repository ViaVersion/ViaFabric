dependencies {
    minecraft("com.mojang:minecraft:1.20.5-rc2")
    mappings("net.fabricmc:yarn:1.20.5-rc2+build.2:v2")

    modImplementation("net.fabricmc.fabric-api:fabric-api:0.97.4+1.20.5")
    modImplementation("com.terraformersmc:modmenu:9.0.0")
}

tasks.compileJava {
    options.release.set(21)
}