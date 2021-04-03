# ViaFabric
[![Travis Build Status](https://travis-ci.com/ViaVersion/ViaFabric.svg?branch=master)](https://travis-ci.com/ViaVersion/ViaFabric)
[![ViaVersion Discord](https://img.shields.io/badge/chat-on%20discord-blue.svg)](https://viaversion.com/discord)
[![CurseForge Downloads](http://cf.way2muchnoise.eu/full_391298_downloads.svg)](https://viaversion.com/fabric)
[![CurseForge Versions](http://cf.way2muchnoise.eu/versions/391298.svg)](https://viaversion.com/fabric)
<!-- ^ GitHub seems to not support Let's Encrypt certificates -->


**Client-side and server-side ViaVersion implementation for Fabric**

Allows the connection to/from different Minecraft versions on your Minecraft client/server (LAN worlds too)

This mod can be installed on 1.8.9, 1.14.x, 1.15.x, 1.16.x and 1.17 snapshots (in separate branches) with Fabric Loader.
Check the Minecraft version in file name when downloading from CurseForge/GitHub Releases.

Note: ViaVersion is designed for Vanilla Minecraft servers. It probably will not work with modded registry entries
or registry synchronization (fabric-registry-sync mod).


**1.14+ Dependencies:**

| Dependency                                | Download                                                                                   |
| ----------------------------------------- | ------------------------------------------------------------------------------------------ |
| (Bundled 3.2.1 release) ViaVersion 3.2.1+ | https://ci.viaversion.com/job/ViaVersion/ or https://ci.viaversion.com/job/ViaVersion-DEV/ |
| (Bundled) Cotton Client Commands          | https://www.curseforge.com/minecraft/mc-mods/cotton-client-commands                        |
| (Optional) Fabric Command API v1/v0       | https://www.curseforge.com/minecraft/mc-mods/fabric-api                                    |
| (Optional) Fabric Lifecycle Events v1/v0  | https://www.curseforge.com/minecraft/mc-mods/fabric-api                                    |
| Fabric Resource Loader v0                 | https://www.curseforge.com/minecraft/mc-mods/fabric-api                                    |

**1.8.9 Dependencies:**

| Dependency                                | Download                                                                                   |
| ----------------------------------------- | ------------------------------------------------------------------------------------------ |
| (Bundled 3.2.1 release) ViaVersion 3.2.1+ | https://ci.viaversion.com/job/ViaVersion/ or https://ci.viaversion.com/job/ViaVersion-DEV/ |
| Legacy Fabric API (**1.0.0+**)            | https://www.curseforge.com/minecraft/mc-mods/legacy-fabric-api                             |


With [ViaVersion](https://viaversion.com):
- your server can accept newer versions
- your client can connect to older versions


Adding [ViaBackwards](https://viaversion.com/backwards) (and optionally [ViaRewind](https://viaversion.com/rewind)):
- your server can accept older versions
- your client can connect to newer versions


**How can I install ViaBackwards/ViaRewind?:**
- Just drop them into mods folder. Make sure you are using versions compatible with the ViaVersion version you are using.
- There are reposts on CurseForge:
- https://www.curseforge.com/minecraft/mc-mods/viabackwards
- https://www.curseforge.com/minecraft/mc-mods/viarewind


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
- Check https://ci.viaversion.com/job/ViaVersion-DEV/, https://ci.viaversion.com/job/ViaBackwards-DEV/ and
  https://ci.viaversion.com/job/ViaRewind-DEV/ for development builds with snapshot support


**Commands**:
- There're 3 server-side alias ``/viaversion``, ``/vvfabric`` and ``/viaver``, and a client-side command
  ``/viafabricclient`` for Minecraft 1.14+ (OP permission level 3 is required for these commands, received
  by [Entity Status Packet](https://wiki.vg/Entity_statuses#Player))


**Configuration**:
- ViaVersion configuration is available at ``.minecraft/config/viafabric/viaversion.yml``
- ViaFabric configuration is at ``.minecraft/config/viafabric/viafabric.yml``


**Alternatives/similar mods/proxies/plugins:**
- [ClientViaVersion](https://github.com/Gerrygames/ClientViaVersion): This discontinued client-side plugin for The 5zig
  Mod implemented ViaVersion, ViaBackwards and ViaRewind for 1.7.10, 1.8.9, 1.12 and 1.12.2 clients, allowing them to
  connect to 1.7-1.12.2 servers. It also had a protocol translation for 1.7 servers, which there's an updated version
  at https://github.com/KennyTV/ViaVersion/tree/hack (unsupported).
- [DirtMultiversion](https://github.com/DirtPowered/DirtMultiversion): Proxy allowing to connect down to Beta 1.3
  with newer Minecraft client versions (currently).
- [multiconnect](https://www.curseforge.com/minecraft/mc-mods/multiconnect): This client-side Fabric mod does also 
  accept older protocols and fixes some differences between versions, which ViaFabric doesn't. Currently, it goes
  down to 1.11 (stable) and 1.9 (experimental). (2020-10-16) (Supports only latest Minecraft client version)
- [Protocol4](https://www.minecraftforum.net/forums/mapping-and-modding-java-edition/minecraft-mods/2299203-protocol4-1-0-2-allows-1-7-10-clients-to-connect):
  This LiteLoader client-side mod allows your 1.7.10 client to connect to 1.7.x servers.
- [ProtocolSupport](https://protocol.support/): This Bukkit plugin allows clients to connect from older versions (down to 1.4.7).
- [VIAaaS](https://github.com/ViaVersion/VIAaaS): Standalone ViaVersion proxy with ViaBackwards and ViaRewind, allowing
  you to connect without a mod installed on your client. Supports online mode.
- [ViaVersion](https://viaversion.com): ViaVersion can run as a plugin for BungeeCord, CraftBukkit, SpongeCommon and Velocity servers.


**Cool things to try:**
- [Geyser](https://geysermc.org/): Plugins, Fabric mod and a standalone proxy for Bedrock edition translation.
- [PolyMc](https://github.com/TheEpicBlock/PolyMc): Fabric mods which translates modded items and blocks, allowing
  vanilla to connect using resource packs.


**How can I disable client-side ViaFabric?:**
- You can disable it in the menu or by setting global protocol version to -1 (this will keep per-server translations still enabled)


**How to use protocol auto detector?:**
- For using globally, set the protocol to AUTO or -2. For using in a specific server: ddns.example.com._v-2.viafabric
- The protocol auto detector will try to ping with the client native protocol version so if you have ViaVersion or
  similar in the server it will use the translator, differently than multiconnect which uses -1 version,
  which may get the native server version.
- It may hold your handshake for up to 10 seconds.
- The results are cached for 100 seconds.


**How can I set the version for specific servers?:**
- Append ._v(VERSION).viafabric. Example: ``minigame.example.com._v1_8.viafabric``, ``native.example.com._v-1.viafabric``, ``auto.example.com._v-2.viafabric``


**Does it work with multiconnect at same time on client?:**
- Yes, ViaFabric can be used with multiconnect. ViaFabric will send to their version auto detector their closest
  supported version. (multiconnect beta-supported versions (currently < 1.11) aren't used)
- Example of configurations:
- (1.8 server) <-> (disabled ViaFabric) <-> (auto-detected 1.8 server - multiconnect) = doesn't work because
  multiconnect doesn't support it
- (1.8 server) <-> (detected 1.8 - ViaFabric - suggests 1.11) <-> (detected 1.11 server - multiconnect) = works,
  ViaVersion is translating 1.8 -> 1.11 and multiconnect is accepting 1.11
- (1.8 server) <-> (forced 1.8 - ViaFabric - suggests 1.11) <-> (detected 1.11 server - multiconnect) = works, ViaVersion
  is translating 1.8 -> 1.11 and multiconnect is accepting 1.11
- (1.8 server) <-> (forced 1.8 - ViaFabric - detected 1.12.2 client) <-> (forced 1.12.2 server - multiconnect) = works,
  ViaVersion is translating 1.8 -> 1.12.2 and multiconnect is accepting 1.12.2


**Differences with multiconnect:**
- ViaFabric main objective is to simply implement a ViaVersion platform
- ViaVersion works kinda of in a "MITM proxy" way
- ViaVersion is designed for servers
- multiconnect modifies client code more deeply, reverting movement changes
- multiconnect is designed for clients and only works on latest client version
- multiconnect is less likely to trigger anticheats


## WARNING
**I cannot guarantee that this mod is allowed on every (or even any) server. This mod may cause problems with anti cheat plugins. USE AT OWN RISK**
