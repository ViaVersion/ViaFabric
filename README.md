# ViaFabric
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


| Dependency                                    | Download                                                            |
| --------------------------------------------- | ------------------------------------------------------------------- |
| (Bundled) ViaVersion                          | https://viaversion.com/                                             |
| (Bundled) Cotton Client Commands (MC 1.14-15) | https://www.curseforge.com/minecraft/mc-mods/cotton-client-commands |
| Fabric API (MC 1.14+)                         | https://www.curseforge.com/minecraft/mc-mods/fabric-api             |
| Legacy Fabric API (MC 1.8.9)                  | https://www.curseforge.com/minecraft/mc-mods/legacy-fabric-api      |


## ViaVersion
**How can I install ViaBackwards/ViaRewind?:**
- Just drop them into mods folder. Make sure you are using versions compatible with the ViaVersion version you are using.
- CurseForge links:
  [ViaBackwards](https://www.curseforge.com/minecraft/mc-mods/viabackwards)
  [ViaRewind](https://www.curseforge.com/minecraft/mc-mods/viarewind)


**What versions can ViaVersion, ViaBackwards and ViaRewind translate?:**
- **With [ViaVersion](https://viaversion.com)**:
  Your server can accept newer versions.
  Your client can connect to older versions.

- **Adding [ViaBackwards](https://viaversion.com/backwards) (and optionally [ViaRewind](https://viaversion.com/rewind))**:
  Your server can accept older versions.
  Your client can connect to newer versions.

- Server-side: See https://viaversion.com/

- Client-side:

|        | 1.8.x | 1.9.x | 1.10-1.14.4 | 1.15.x | 1.16.x | 1.17.x |
| ------ | ----- | ----- | ----------- | ------ | ------ | ------ |
| 1.8.9 client | ✓ | ⏪ | ⟲ | ⟲ | ⟲ | ⟲ |
| 1.14.x client | ✓ | ✓ | ✓ | ⟲ | ⟲ | ⟲ |
| 1.15.x client | ✓ | ✓ | ✓ | ✓ | ⟲ | ⟲ |
| 1.16.x client | ✓ | ✓ | ✓ | ✓ | ✓ | ⟲ |
| 1.17.x client | ✓ | ✓ | ✓ | ✓ | ✓ | ✓ |

✓ = [ViaVersion](https://viaversion.com) ⟲ = [ViaBackwards](https://viaversion.com/backwards) ⏪ = [ViaRewind](https://viaversion.com/rewind)


**Can ViaVersion, ViaBackwards and ViaRewind support snapshots?:**
- Check https://ci.viaversion.com/ for development builds with snapshot support

## Alternatives
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
- [Geyser](https://geysermc.org/): Plugins, Fabric mod and a standalone proxy for allowing Bedrock Edition on Java Edition servers.
- [PolyMc](https://github.com/TheEpicBlock/PolyMc): Fabric mods which translates modded items and blocks, allowing
  vanilla to connect using resource packs.


## Commands
**Commands**:
- There're 3 server-side alias ``/viaversion``, ``/vvfabric`` and ``/viaver``, and a client-side command
  ``/viafabricclient`` for Minecraft 1.14+ (OP permission level 3 is required for these commands, received
  by [Entity Status Packet](https://wiki.vg/Entity_statuses#Player))


## Configs
**Configuration**:
- ViaVersion configuration is available at ``.minecraft/config/viafabric/viaversion.yml``
- ViaFabric configuration is at ``.minecraft/config/viafabric/viafabric.yml``


**How can I disable client-side ViaFabric?:**
- You can disable it in the menu or by setting global protocol version to -1 (this will keep per-server translations still enabled)


**How to use protocol auto detector?:**
- For using globally, set the protocol to AUTO or -2. For using in a specific server: ``ddns.example.com._v-2.viafabric``
- The protocol auto detector will try to ping with the client native protocol version. If you have ViaVersion or
  similar in the server it may use the translated version, differently than multiconnect which uses -1 version,
  which may get the native server version.
- It may hold your handshake for up to 10 seconds.
- The auto-detected version is cached for 100 seconds.


**How can I set the version for specific servers?:**
- Append ._v(VERSION).viafabric.
- Examples: ``minigame.example.com._v1_8.viafabric``, ``native.example.com._v-1.viafabric``, ``auto.example.com._v-2.viafabric``


## multiconnect
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
- ViaFabric can be installed on more versions
- ViaVersion works kinda of in a "MITM proxy" way
- ViaVersion is designed for servers
- multiconnect only supports the latest Minecraft version
- multiconnect modifies client code more deeply, reverting movement changes
- multiconnect is designed for clients and only works on latest client version
- multiconnect is less likely to trigger anticheats


## WARNING
**I cannot guarantee that this mod is allowed on every (or even any) server. This mod may cause problems with anti cheat plugins. USE AT OWN RISK**
