# ViaFabric
[![Travis Build Status](https://travis-ci.com/ViaVersion/ViaFabric.svg?branch=master)](https://travis-ci.com/ViaVersion/ViaFabric)
[![ViaVersion Discord](https://img.shields.io/badge/chat-on%20discord-blue.svg)](https://viaversion.com/discord)
[![GitHub Releases](https://img.shields.io/github/downloads/ViaVersion/ViaFabric/total)](https://github.com/ViaVersion/ViaFabric/releases)
[![CurseForge Downloads](http://cf.way2muchnoise.eu/short_391298.svg)](https://viaversion.com/fabric)
<!-- ^ GitHub seems to not support Let's Encrypt certificates -->


**Client-side and server-side ViaVersion implementation for Fabric**

Allows the connection to/from different Minecraft versions on your Minecraft client/server (LAN worlds too)

This mod supports 1.8.9 (in `mc-1.8` branch), 1.14.4/1.15.2 (in `mc-1.14-1.15` branch) and 1.16.x/snapshots (in `mc-1.16` branch) with Fabric Loader. Check the Minecraft version in file name when downloading from CurseForge/GitHub Releases.

Note: ViaVersion is not designed for modded Minecraft with registry synchronization (fabric-registry-sync mod).


**1.14+ Dependencies:**
| Dependency                         | Download                                                              |
| ---------------------------------- | --------------------------------------------------------------------- |
| ViaVersion 3.0.2+                  | dev builds at https://ci.viaversion.com/job/ViaVersion/               |
| Fabric Textures v0                 | https://www.curseforge.com/minecraft/mc-mods/fabric-api               |
| Fabric Resource Loader v0          | https://www.curseforge.com/minecraft/mc-mods/fabric-api               |
| Fabric Command API v1              | https://www.curseforge.com/minecraft/mc-mods/fabric-api               |
| (Included) Cotton Client Commands  | https://www.curseforge.com/minecraft/mc-mods/cotton-client-commands   |


**1.8.9 Dependencies:**
| Dependency                             | Download                                                |
| -------------------------------------- | ------------------------------------------------------- |
| ViaVersion 3.0.2+                      | dev builds at https://ci.viaversion.com/job/ViaVersion/ |
| Fabric Events Lifecycle v0 (mc 1.8.x)  | unknown? (https://github.com/Legacy-Fabric/fabric)      |
| Fabric Resource Loader v0              | unknown? (https://github.com/Legacy-Fabric/fabric)      |


With ViaVersion:
- your server can accept newer versions
- your client can connect to older versions


Adding [ViaBackwards](https://viaversion.com/backwards) (and optionally [ViaRewind](https://viaversion.com/rewind)):
- your server can accept older versions
- your client can connect to newer versions


**What versions can ViaVersion, ViaBackwards and ViaRewind translate?:**
- See https://viaversion.com


**Can ViaVersion and ViaBackwards support snapshots?:**
- Check https://ci.viaversion.com/job/ViaVersion-DEV/ and https://ci.viaversion.com/job/ViaBackwards-DEV/ for development builds with snapshot support


**Commands**:
- There're 3 server-side alias ``/viaversion``, ``/vvfabric`` and ``/viaver``, and a client-side command ``/viafabricclient`` (OP permission level 3 is required for these commands, in the client it's received by [Entity Status Packet](https://wiki.vg/Entity_statuses#Player))


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


**Does it work with multiconnect at same time on client?:**
- Yes, ViaFabric can be used with multiconnect. ViaFabric will set multiconnect version auto detector to the supported version which is closest to client-side version.
- Example of setups:
- (1.8 server) <-> (disabled ViaFabric) <-> (auto detected 1.8 server - multiconnect on Minecraft) = doesn't work because multiconnect doesn't support it
- (1.8 server) <-> (forced 1.8 - ViaFabric in client - suggests 1.10) <-> (detected 1.10 server - multiconnect on Minecraft) = works, with ViaVersion translating 1.8 -> 1.10 and multiconnect accepting 1.10
- (1.8 server) <-> (forced 1.8 - ViaFabric in client - detected 1.12.2 client) <-> (forced 1.12.2 server - multiconnect on Minecraft) = works with ViaVersion translating 1.8 -> 1.12.2 and multiconnect accepting 1.12.2

## WARNING
**I cannot guarantee that this mod is allowed on every (or even any) server. This mod may cause problems with anti cheat plugins. USE AT OWN RISK**
