dependencies {
    minecraft("com.mojang:minecraft:23w14a")
    mappings("net.fabricmc:yarn:23w14a+build.4:v2")

    modImplementation("net.fabricmc.fabric-api:fabric-api:0.77.0+1.20")
    modImplementation("com.terraformersmc:modmenu:6.1.0-rc.4")
}

tasks.compileJava {
    options.release.set(17)
}
