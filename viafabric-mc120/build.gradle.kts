dependencies {
    minecraft("com.mojang:minecraft:1.20-rc1")
    mappings("net.fabricmc:yarn:1.20-rc1+build.2:v2")

    modImplementation("net.fabricmc.fabric-api:fabric-api:0.83.0+1.20")
    modImplementation("com.terraformersmc:modmenu:7.0.0-beta.2")
}

tasks.compileJava {
    options.release.set(17)
}