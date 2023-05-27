dependencies {
    minecraft("com.mojang:minecraft:1.20-pre6")
    mappings("net.fabricmc:yarn:1.20-pre6+build.2:v2")

    modImplementation("net.fabricmc.fabric-api:fabric-api:0.82.1+1.20")
    modImplementation("com.terraformersmc:modmenu:7.0.0-beta.2")
}

tasks.compileJava {
    options.release.set(17)
}