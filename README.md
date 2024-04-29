# ViaFabric

[![ViaVersion Discord](https://img.shields.io/badge/chat-on%20discord-blue.svg)](https://viaversion.com/discord)
[![CurseForge Downloads](http://cf.way2muchnoise.eu/full_viafabric_downloads.svg)](https://viaversion.com/fabric)
[![Modrinth Downloads](https://img.shields.io/modrinth/dt/ViaFabric?label=Modrinth&logo=Modrinth&style=flat-square)](https://modrinth.com/mod/ViaFabric)
[![CurseForge Versions](http://cf.way2muchnoise.eu/versions/viafabric.svg)](https://viaversion.com/fabric)
<!-- ^ GitHub seems to not like this https -->

### Client-side and server-side ViaVersion implementation for Fabric

Allows the connection to/from different Minecraft versions on your Minecraft client/server (LAN worlds too)

This mod can be installed on 1.8.9, 1.12.2, 1.14.4, 1.15.2, 1.16.5, 1.17.1, 1.18.2, 1.19.4, 1.20.1, 1.20.4, 1.20.6 with Fabric Loader.

## Dependencies

| Dependency                                    | Download                                                       |
|-----------------------------------------------|----------------------------------------------------------------|
| (Bundled) ViaVersion                          | https://viaversion.com/                                        |
| (Bundled) Cotton Client Commands (MC 1.14-15) | https://jitpack.io/#TinfoilMC/ClientCommands                   |
| Fabric API (MC 1.14+)                         | https://modrinth.com/mod/fabric-api                            |
| Legacy Fabric API (MC 1.8.9 and 1.12.2)       | https://modrinth.com/mod/legacy-fabric-api                     |

Note: ViaVersion is designed for Vanilla Minecraft servers. It probably will not work with modded registry entries or
registry synchronization (fabric-registry-sync mod).

## ViaVersion

### How can I install ViaBackwards/ViaRewind?:

- Just drop them into mods folder. Make sure you are using versions compatible with the ViaVersion version you are
  using.

### What versions can ViaVersion, ViaBackwards and ViaRewind translate?:

- **With [ViaVersion](https://viaversion.com)**:
  Your server can accept newer versions. Your client can connect to older versions.

- **Adding [ViaBackwards](https://viaversion.com/backwards) (and
  optionally [ViaRewind](https://viaversion.com/rewind))**:
  Your server can accept older versions. Your client can connect to newer versions.

- Server-side: See https://viaversion.com/

- Client-side:

|               | 1.8.9 | 1.9.x | 1.10-1.12.2 | 1.13-1.14.4 | 1.15.x | 1.16.x | 1.17.x | 1.18.x | 1.19.x | 1.20.x                                                 |
|---------------|-------|-------|-------------|-------------|--------|--------|--------|--------|--------|--------------------------------------------------------|
| 1.8.9 client  | ✓     | ⏪     | ⏪⟲          | ⏪⟲          | ⏪⟲     | ⏪⟲     | ⏪⟲     | ⏪⟲     | ⏪⟲     | ⏪⟲                                                     |
| 1.12.2 client | ✓     | ✓     | ✓           | ⏪           | ⏪      | ⏪      | ⏪      | ⏪      | ⏪      | ⏪                                                      |
| 1.14.4 client | ✓     | ✓     | ✓           | ✓           | ⟲      | ⟲      | ⟲      | ⟲      | ⟲      | ⟲                                                      |
| 1.15.2 client | ✓     | ✓     | ✓           | ✓           | ✓      | ⟲      | ⟲      | ⟲      | ⟲      | ⟲                                                      |
| 1.16.5 client | ✓     | ✓     | ✓           | ✓           | ✓      | ✓      | ⟲      | ⟲      | ⟲      | ⟲                                                      |
| 1.17.1 client | ✓     | ✓     | ✓           | ✓           | ✓      | ✓      | ✓      | ⟲      | ⟲      | ⟲                                                      |
| 1.18.2 client | ✓     | ✓     | ✓           | ✓           | ✓      | ✓      | ✓      | ✓      | ⟲      | ⟲                                                      |
| 1.19.4 client | ✓     | ✓     | ✓           | ✓           | ✓      | ✓      | ✓      | ✓      | ✓      | ⟲                                                      |
| 1.20.1 client | ✓     | ✓     | ✓           | ✓           | ✓      | ✓      | ✓      | ✓      | ✓      | ✓ *(⟲ if connecting to newer 1.20.2 - 1.20.6 servers)* |
| 1.20.4 client | ✓     | ✓     | ✓           | ✓           | ✓      | ✓      | ✓      | ✓      | ✓      | ✓ *(⟲ if connecting to 1.20.6 servers)*                |
| 1.20.6 client | ✓     | ✓     | ✓           | ✓           | ✓      | ✓      | ✓      | ✓      | ✓      | ✓                                                      |

✓ = [ViaVersion](https://viaversion.com) ⟲ = [ViaBackwards](https://viaversion.com/backwards) ⏪
= [ViaRewind](https://viaversion.com/rewind)

*Note: 1.7.x is not supported in ViaFabric - 1.9.x, 1.10.x, 1.11.x, and 1.13.x may cause problems when visiting such servers in client-side mode.*

### Can ViaVersion, ViaBackwards and ViaRewind support snapshots?:

- Check https://ci.viaversion.com/ for development builds with snapshot support

## Alternatives

### Client-side:

- [ClientViaVersion](https://github.com/Gerrygames/ClientViaVersion): Discontinued 5zig plugin.
- [multiconnect](https://www.curseforge.com/minecraft/mc-mods/multiconnect): Discontinued Fabric mod for connecting to older
  versions: down to 1.11 (stable) and 1.8 (experimental).
- [ViaForge](https://www.modrinth.com/mod/viaforge): Clientside Implementation of ViaVersion for Forge.
- [ViaFabricPlus](https://www.modrinth.com/mod/viafabricplus): Fabric ViaVersion/ViaLegacy/ViaAprilFools/ViaBedrock implementation with client-side fixes.  

### Server-side:

- [ProtocolSupport](https://protocol.support/): Bukkit plugin for older client versions (down to 1.4.7).
- [ViaVersion](https://viaversion.com): Plugin for BungeeCord, CraftBukkit, SpongeCommon and Velocity servers.

### Standalone proxy:

- [DirtMultiversion](https://github.com/DirtPowered/DirtMultiversion): Proxy allowing to connect down to Beta 1.3 with
  newer Minecraft client versions.
- [VIAaaS](https://github.com/ViaVersion/VIAaaS): Standalone ViaVersion proxy with ViaBackwards and ViaRewind, allowing
  you to connect without a mod installed on your client. Supports online mode.
- [ViaProxy](https://github.com/ViaVersion/ViaProxy): Standalone ViaVersion proxy with ViaBackwards, ViaRewind and ViaLegacy,
  allowing you to connect to a wide range of versions (down to classic versions) with your modern client (1.7.2+). Supports online mode.

### Cool things to try:

- [Geyser](https://geysermc.org/): Plugins, Fabric mod and a standalone proxy for allowing Bedrock Edition on Java
  Edition servers.
- [PolyMc](https://github.com/TheEpicBlock/PolyMc): Fabric mods which translates modded items and blocks, allowing
  vanilla to connect using resource packs.

## Commands

### Commands:

- There're 3 server-side alias ``/viaversion``, ``/vvfabric`` and ``/viaver``, and a client-side command
  ``/viafabricclient`` for Minecraft 1.14+ (OP permission level 3 is required for these commands, received
  by [Entity Status Packet](https://wiki.vg/Entity_statuses#Player))

## Configs

### Configuration:

- ViaVersion configuration is available at ``.minecraft/config/viafabric/viaversion.yml``
- ViaFabric configuration is at ``.minecraft/config/viafabric/viafabric.yml``

### How can I disable client-side ViaFabric?:

- You can disable it in the menu or by setting global protocol version to -1 (this will keep per-server translations
  still enabled)

### How to use protocol detection?:

- For using globally, set the protocol to AUTO or -2. For using in a specific
  server: ``ddns.example.com._v-2.viafabric``
- The protocol detector will try to ping with the client native protocol version, differently than multiconnect which
  uses -1 version, which may detect the native server version.

### How can I set the version for specific servers?:

- Append ._v(VERSION).viafabric.
- Examples: ``minigame.example.com._v1_8.viafabric``, ``native.example.com._v-1.viafabric``
  , ``auto.example.com._v-2.viafabric``

## ViaFabricPlus

### Does it work with ViaFabric:

- No, ViaFabric cannot be used with ViaFabricPlus.

### Differences with ViaFabricPlus:

|                                  | ViaFabric                                       | ViaFabricPlus                                                   |
|----------------------------------|-------------------------------------------------|-----------------------------------------------------------------|
| Can be installed on              | Multiple client/server versions with fabric     | Latest client-side version with fabric                          |
| Objectives                       | Simply implement ViaVersion                     | Implements ViaVersion with client-side fixes to version changes |
| How does it work?                | Modifying packets at network code               | Modifying client code more deeply                               |
| Triggering anti-cheats           | Very likely                                     | Mostly not                                                      |

## Disclaimer

It cannot be guaranteed that this mod is allowed on specific servers as it can possibly cause problems with anti-cheat plugins.\
***(USE ONLY WITH CAUTION!)***
