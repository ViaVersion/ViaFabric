dependencies {
    minecraft("com.mojang:minecraft:24w14a")
    mappings("net.fabricmc:yarn:24w14a+build.5:v2")

    modImplementation("net.fabricmc.fabric-api:fabric-api:0.96.14+1.20.5")
    modImplementation("com.terraformersmc:modmenu:9.0.0")
}

tasks.compileJava {
    options.release.set(21)
}
