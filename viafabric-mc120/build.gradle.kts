dependencies {
    minecraft("com.mojang:minecraft:23w18a")
    mappings("net.fabricmc:yarn:23w18a+build.4:v2")

    modImplementation("net.fabricmc.fabric-api:fabric-api:0.80.0+1.20")
    modImplementation("com.terraformersmc:modmenu:7.0.0-beta.2")
}

tasks.compileJava {
    options.release.set(17)
}
