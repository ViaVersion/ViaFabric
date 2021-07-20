version = rootProject.version

loom {
    customManifest = "https://gist.githubusercontent.com/modmuss50/6e00baf80dcbaa42f3a2fc846b290128/raw/f035ef8f75164f56ec6352809a34841d326bae1c/1_18_experimental-snapshot-1.json"
}

dependencies {
    minecraft("com.mojang:minecraft:1.18_experimental-snapshot-1")
    mappings("net.fabricmc:yarn:1.18_experimental-snapshot-1+build.9:v2")

    modImplementation("net.fabricmc.fabric-api:fabric-api:0.37.2+1.18_experimental")
    modImplementation("com.terraformersmc:modmenu:2.0.2")
}
