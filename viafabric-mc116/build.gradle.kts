version = rootProject.version

dependencies {
    minecraft("com.mojang:minecraft:1.14.4")
    mappings("net.fabricmc:yarn:1.14.4+build.16:v2")
    modImplementation("net.fabricmc:fabric-loader:0.8.2+build.194")

    modImplementation("net.fabricmc.fabric-api:fabric-api:0.13.1+build.257-1.14")
    modImplementation("io.github.prospector:modmenu:1.7.16.1.14.4+build.128")
    modImplementation("io.github.cottonmc:cotton-client-commands:1.0.0+1.15.2")
}