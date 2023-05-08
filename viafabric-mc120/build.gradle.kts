dependencies {
    minecraft("com.mojang:minecraft:23w18a")
    mappings("net.fabricmc:yarn:23w18a+build.2:v2")

    modImplementation("net.fabricmc.fabric-api:fabric-api:0.80.0+1.20")
    modImplementation("com.terraformersmc:modmenu:6.1.0-rc.4")
}

tasks.compileJava {
    options.release.set(17)
}
