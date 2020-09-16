# ViaFabric
[![Travis Build Status](https://travis-ci.com/ViaVersion/ViaFabric.svg?branch=master)](https://travis-ci.com/ViaVersion/ViaFabric)
[![ViaVersion Discord](https://img.shields.io/badge/chat-on%20discord-blue.svg)](https://viaversion.com/discord)
[![CurseForge Downloads](http://cf.way2muchnoise.eu/full_391298_downloads.svg)](https://viaversion.com/fabric)
[![CurseForge Versions](http://cf.way2muchnoise.eu/versions/391298.svg)](https://viaversion.com/fabric)
<!-- ^ GitHub seems to not support Let's Encrypt certificates -->


**Client-side and server-side ViaVersion implementation for Fabric**

Allows the connection to/from different Minecraft versions on your Minecraft client/server (LAN worlds too)

This mod supports 1.8.9 (in `mc-1.8` branch), 1.14.x/1.15.x (in `mc-1.14-1.15` branch) and 1.16.x/snapshots (in `mc-1.16` branch) with Fabric Loader. Check the Minecraft version in file name when downloading from CurseForge/GitHub Releases.

Note: ViaVersion is designed for Vanilla Minecraft servers. It probably will not work with modded registry entries or registry synchronization (fabric-registry-sync mod).


**1.14+ Dependencies:**

| Dependency                                | Download                                                                                   |
| ----------------------------------------- | ------------------------------------------------------------------------------------------ |
| (Bundled 3.1.1 release) ViaVersion 3.0.2+ | https://ci.viaversion.com/job/ViaVersion/ or https://ci.viaversion.com/job/ViaVersion-DEV/ |
| (Bundled) Cotton Client Commands          | https://www.curseforge.com/minecraft/mc-mods/cotton-client-commands                        |
| (Optional) Fabric Command API v1/v0       | https://www.curseforge.com/minecraft/mc-mods/fabric-api                                    |
| Fabric Resource Loader v0                 | https://www.curseforge.com/minecraft/mc-mods/fabric-api                                    |

**1.8.9 Dependencies:**

| Dependency                                | Download                                                                                   |
| ----------------------------------------- | ------------------------------------------------------------------------------------------ |
| (Bundled 3.1.1 release) ViaVersion 3.0.2+ | https://ci.viaversion.com/job/ViaVersion/ or https://ci.viaversion.com/job/ViaVersion-DEV/ |
| (Optional) Fabric Commands v0             | https://www.curseforge.com/minecraft/mc-mods/legacy-fabric-api                             |
| Fabric Resource Loader v0                 | https://www.curseforge.com/minecraft/mc-mods/legacy-fabric-api                             |


With [ViaVersion](https://viaversion.com):
- your server can accept newer versions
- your client can connect to older versions


Adding [ViaBackwards](https://viaversion.com/backwards) (and optionally [ViaRewind](https://viaversion.com/rewind)):
- your server can accept older versions
- your client can connect to newer versions


**How can I install ViaBackwards/ViaRewind?:**
- Just drop it into mods folder. Make sure you are using versions compatible with the ViaVersion version you are using. ViaVersion-DEV/ViaBackwards-DEV/ViaRewind-DEV and ViaVersion/ViaBackwards/ViaRewind CI builds are only compatible to each other if they have the same label (``-DEV`` or empty).


**What versions can ViaVersion, ViaBackwards and ViaRewind translate?:**
- Server-side:
[![Graph with ViaVersion supported versions](https://i.imgur.com/0u20Y2u.png)](https://viaversion.com)

- Client-side:

| Your Client | 1.8.x | 1.9.x | 1.10-1.14.4 | 1.15.x | 1.16.x |
| ----------- | ----- | ----- | ----------- | ------ | ------ |
| 1.8.9  | ✓ | ⏪ | ⟲ | ⟲ | ⟲ |
| 1.14.x | ✓ | ✓ | ✓ | ⟲ | ⟲ |
| 1.15.x | ✓ | ✓ | ✓ | ✓ | ⟲ |
| 1.16.x | ✓ | ✓ | ✓ | ✓ | ✓ |

✓ = [ViaVersion](https://viaversion.com) ⟲ = [ViaBackwards](https://viaversion.com/backwards) ⏪ = [ViaRewind](https://viaversion.com/rewind)


**Can ViaVersion, ViaBackwards and ViaRewind support snapshots?:**
- Check https://ci.viaversion.com/job/ViaVersion-DEV/, https://ci.viaversion.com/job/ViaBackwards-DEV/ and https://ci.viaversion.com/job/ViaRewind-DEV/ for development builds with snapshot support


**Commands**:
- There're 3 server-side alias ``/viaversion``, ``/vvfabric`` and ``/viaver``, and a client-side command ``/viafabricclient`` for Minecraft 1.14+ (OP permission level 3 is required for these commands, received by [Entity Status Packet](https://wiki.vg/Entity_statuses#Player))


**Configuration**:
- ViaVersion configuration is available at ``.minecraft/config/viafabric/viaversion.yml``
- ViaFabric configuration is at ``.minecraft/config/viafabric/viafabric.yml``


**Alternatives to this mod:**
- [ClientViaVersion](https://github.com/Gerrygames/ClientViaVersion): This discontinued client-side plugin for The 5zig Mod implemented ViaVersion, ViaBackwards and ViaRewind for 1.7.10, 1.8.9, 1.12 and 1.12.2 clients, allowing them to connect to 1.7-1.12.2 servers. It also had a protocol translation for 1.7 servers, which there's an updated version at https://github.com/KennyTV/ViaVersion/tree/hack (unsupported).
- [multiconnect](https://www.curseforge.com/minecraft/mc-mods/multiconnect): This client-side Fabric mod does also accept older protocols and fixes some differences between versions, which ViaFabric doesn't. Currently, it goes down to 1.10. (2020-06-23)
- [Protocol4](https://www.minecraftforum.net/forums/mapping-and-modding-java-edition/minecraft-mods/2299203-protocol4-1-0-2-allows-1-7-10-clients-to-connect): This LiteLoader client-side mod allows your 1.7.10 client to connect to 1.7.x servers.
- [ProtocolSupport](https://protocol.support/): This Bukkit plugin allows clients to connect from older versions (down to 1.4.7).
- [ViaVersion](https://viaversion.com): ViaVersion can run as a plugin for BungeeCord, CraftBukkit, SpongeCommon and Velocity servers.


**How can I disable client-side ViaFabric?:**
- You can disable it by resetting the anti-cheat warning in config file or by setting protocol version to -1


**How can I set the version for specific servers?:**
- Append ._v(VERSION).viafabric. Example: ``minigame.example.com._v1_8.viafabric``, ``native.example.com._v-1.viafabric``


**Does it work with multiconnect at same time on client?:**
- Yes, ViaFabric can be used with multiconnect. ViaFabric will send to their version auto detector their closest supported version. (multiconnect beta-supported versions (currently 1.10) aren't used)
- Example of setups:
- (1.8 server) <-> (disabled ViaFabric) <-> (auto detected 1.8 server - multiconnect) = doesn't work because multiconnect doesn't support it
- (1.8 server) <-> (forced 1.8 - ViaFabric - suggests 1.11) <-> (detected 1.11 server - multiconnect) = works, ViaVersion translating 1.8 -> 1.11 and multiconnect accepting 1.11
- (1.8 server) <-> (forced 1.8 - ViaFabric - detected 1.12.2 client) <-> (forced 1.12.2 server - multiconnect) = works, ViaVersion translating 1.8 -> 1.12.2 and multiconnect accepting 1.12.2

## WARNING
**I cannot guarantee that this mod is allowed on every (or even any) server. This mod may cause problems with anti cheat plugins. USE AT OWN RISK**
