dependencies {
    minecraft("com.mojang:minecraft:1.20.2-rc1")
    mappings("net.fabricmc:yarn:1.20.2-rc1+build.3:v2")

    modImplementation("net.fabricmc.fabric-api:fabric-api:0.86.1+1.20.1")
    modImplementation("com.terraformersmc:modmenu:7.2.1")
}

tasks.compileJava {
    options.release.set(17)
}
