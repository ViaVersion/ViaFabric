version = rootProject.version

dependencies {
    minecraft("com.mojang:minecraft:1.15.2")
    mappings("net.fabricmc:yarn:1.15.2+build.17:v2")

    modImplementation("net.fabricmc.fabric-api:fabric-api:0.19.0+build.325-1.15")
    modImplementation("io.github.prospector:modmenu:1.10.2+build.32")
    modImplementation("io.github.cottonmc:cotton-client-commands:1.0.0+1.15.2")
}