# ViaFabric
[![Travis Build Status](https://travis-ci.com/ViaVersion/ViaFabric.svg?branch=master)](https://travis-ci.com/ViaVersion/ViaFabric)
[![ViaVersion Discord](https://img.shields.io/badge/chat-on%20discord-blue.svg)](https://viaversion.com/discord)
[![GitHub Releases](https://img.shields.io/github/downloads/ViaVersion/ViaFabric/total)](https://github.com/ViaVersion/ViaFabric/releases)
[![CurseForge Downloads](https://cf.way2muchnoise.eu/short_391298.svg)](https://viaversion.com/fabric)


**Client-side and server-side ViaVersion implementation for Fabric**

Allows the connection to/from different Minecraft versions on your Minecraft client/server (LAN worlds too)

This mod supports 1.14.4/1.15.x (on `ver/1.14` branch) and 1.16 snapshots (on `master` branch) with Fabric Loader. Check the Minecraft version in file name when downloading from GitHub Releases.

Note: ViaVersion is not designed for modded Minecraft with registry synchronization (fabric-registry-sync mod).


**Dependencies:**
| Dependency                        | Download                                                              |
| --------------------------------- | --------------------------------------------------------------------- |
| ViaVersion 3.0.0+                 | https://viaversion.com/                                               |
| Fabric Textures v0                | https://www.curseforge.com/minecraft/mc-mods/fabric-api               |
| Fabric Resource Loader v0         | https://www.curseforge.com/minecraft/mc-mods/fabric-api               |
| Fabric Command API v1             | https://www.curseforge.com/minecraft/mc-mods/fabric-api               |
| (Included) Cotton Client Commands | https://www.curseforge.com/minecraft/mc-mods/cotton-client-commands   |


With ViaVersion:
- your server can accept newer versions
- your client can connect to older versions


Adding [ViaBackwards](https://viaversion.com/backwards) (and optionally [ViaRewind](https://viaversion.com/rewind)):
- your server can accept older versions
- your client can connect to newer versions


A chart with compatible versions is available at https://viaversion.com


**Commands**:
There're 3 server-side alias ``/viaversion``, ``/vvfabric`` and ``/viaver``, and a client-side command ``/viafabricclient`` (OP permission level 3 is required for these commands, in the client it's received by [Entity Status Packet](https://wiki.vg/Entity_statuses#Player))


**Configuration**:
ViaVersion configuration is available at ``.minecraft/config/viafabric/viaversion.yml``
ViaFabric configuration is at ``.minecraft/config/viafabric/viafabric.yml``


**Alternatives to this mod:**
- [MultiConnect](https://www.curseforge.com/minecraft/mc-mods/multiconnect): this client-side mod does also translate older protocols and fixes some differences between versions, which ViaFabric don't. Currently, it goes down to 1.10. (2020-06-23)
- [ProtocolSupport](https://protocol.support/): This plugin for Bukkit allows clients to connect from older versions (down to 1.4.7).

## WARNING
**I cannot guarantee that this mod is allowed on every (or even any) server. This mod may cause problems with anti cheat plugins. USE AT OWN RISK**
