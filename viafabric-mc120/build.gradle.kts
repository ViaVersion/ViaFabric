dependencies {
    minecraft("com.mojang:minecraft:23w13a")
    mappings("net.fabricmc:yarn:23w13a+build.3:v2")

    modImplementation("net.fabricmc.fabric-api:fabric-api:0.76.2+1.20")
    modImplementation("com.terraformersmc:modmenu:6.1.0-rc.4")
}

tasks.compileJava {
    options.release.set(17)
}
