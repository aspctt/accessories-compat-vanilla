# <p align=center> Accessories Compat: Vanilla </p>

<div align="center">

![Version](https://img.shields.io/badge/Available_for-1.21.1-blue)
![Requires](https://img.shields.io/badge/Requires-Accessories-blueviolet)
![License](https://img.shields.io/badge/License-LGPL--3.0--only-red)

![NeoForge](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/supported/neoforge_vector.svg)
![Fabric](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/supported/fabric_vector.svg)
![Forge](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/unsupported/forge_vector.svg)

[![GitHub](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/compact-minimal/available/github_vector.svg)](https://github.com/aspctt/accessories-compat-vanilla)
[![Modrinth](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/compact-minimal/available/modrinth_vector.svg)](https://modrinth.com/mod/accessories-compat-vanilla)
[![CurseForge](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/compact-minimal/available/curseforge_vector.svg)](https://www.curseforge.com/minecraft/mc-mods/accessories-compat-vanilla)

</div>

## Description

Accessories Compat: Vanilla lets the elytra and the Totem of Undying be worn in [Accessories](https://github.com/wisp-forest/accessories) slots. It merges [Elytra Slot](https://github.com/illusivesoulworks/elytraslot) and [Charm of Undying](https://github.com/illusivesoulworks/charmofundying) by Illusive Soulworks into one mod, built directly on the Accessories API instead of Curios or Trinkets, with the same features on NeoForge and Fabric.

### Elytra

* Goes in the **cape** slot, and flies exactly as it would in the chest slot, so a chestplate can be worn at the same time.
* Wears down while gliding and stops working at one durability, as in the chest slot.
* Is drawn on the player and on armor stands, using the player's own elytra or cape texture when they have one, and hides the cape it covers. The slot's visibility toggle and cosmetic slots apply to it.
* Plays the elytra's equip sound, and cannot be equipped while an elytra is already worn in the chest slot.
* Modded elytras that use the vanilla model are recognised and drawn with their own texture: Deeper Darker, Enderite Mod, Mythic Metals, Mekanism, Mana and Artifice, Wooden Elytra, LieOnLion's Enderite, Nether Elytra, Crystal Mod and Lil' Wings.
* Any other elytra can be made to fly from the slot by adding it to the `accessoriescompatvanilla:elytra` item tag with a datapack.

### Totem of Undying

* Goes in the **charm** slot, and saves its wearer from death without being held. A totem in the charm slot is used before a held one.
* Plays the full activation for the wearer and everyone nearby: particles, sound, and the totem animation on screen.
* Counts in statistics and triggers the "Postmortal" advancement, like a held totem.
* Is drawn on the wearer's chest.
* Also supports Biome Makeover's Enchanted Totem, Netherite Extras' Totem of Neverdying and Totem of Infinity's infinite totems, and draws Friends & Foes' totems.

Which items fit which slot is decided by the `accessories:cape` and `accessories:charm` item tags, so a datapack can move them.

## Differences from Elytra Slot and Charm of Undying

* **No Caelus API on NeoForge.** Flight comes from three small hooks on the places vanilla checks the chest slot, so other mods that control when a glide starts, like Do Another Barrel Roll's jump activation, keep working.
* **The totem's display options are a client setting** rather than a server one, and need no config library. See [Configuration](#configuration).
* The elytra goes in the cape slot on both loaders. Elytra Slot used Curios' back slot on NeoForge and Trinkets' cape slot on Fabric.

### Not supported

These integrations from the original mods are not part of this one:

* Deeper Darker: boosting with the Soul Elytra from an accessory slot. The Soul Elytra flies and is drawn, but its boost key only works from the chest slot.
* MinecraftCapes: its capes are not used as the accessory elytra's texture (Elytra Slot did this on NeoForge only).
* Wavey Capes: its cape is not hidden under an accessory elytra.
* Fabric Waystones: the Void Totem does not work from the charm slot (Charm of Undying did this on Fabric only).

## Configuration

`config/accessoriescompatvanilla-client.json` is written on first launch:

| Option | Default | Meaning |
|---|---|---|
| `renderTotem` | `true` | Whether an equipped totem is drawn on its wearer |
| `totemOffsetX` | `0.0` | Moves the drawn totem sideways, in blocks |
| `totemOffsetY` | `0.0` | Moves the drawn totem down, in blocks |
| `totemOffsetZ` | `0.0` | Moves the drawn totem backwards, in blocks |

The offsets run from -100 to 100, on the same axes as Charm of Undying's `xOffset`, `yOffset` and `zOffset`.

## Installation

Place the jar for your loader in the mods folder of your Minecraft instance, alongside Accessories and its dependencies. It is needed on both the client and the server.

**REMOVE ANY OLD VERSIONS BEFORE INSTALLING**, and do not install it alongside Elytra Slot or Charm of Undying.

## Dependencies

* Minecraft 1.21.1
* NeoForge 21.1.250 or newer, or Fabric Loader 0.16.0 or newer with Fabric API
* Accessories 1.1.0-beta.53 or newer, which itself needs owo-lib

## Building

The build is organised with [Stonecutter](https://stonecutter.kikugie.dev/), which compiles one source tree for several targets. Each target is a subproject under `versions/`, named `<minecraft version>-<loader>`, declared in `settings.gradle.kts` and configured by its own `gradle.properties`. Each loader has its own build script, `build.neoforge.gradle.kts` and `build.fabric-remap.gradle.kts`, and `common.gradle.kts` holds what they share.

```
./gradlew build                          # build every declared target
./gradlew :1.21.1-neoforge:build         # build one
./gradlew :1.21.1-neoforge:runClient     # run one, sharing the root run/ directory
```

Jars are written to `versions/<target>/build/libs`, named `AccessoriesCompatVanilla-<version>+<minecraft version>-<loader>.jar`.

| Target | Status |
|---|---|
| 1.21.1-neoforge | declared |
| 1.21.1-fabric | declared |

Almost all of the mod is shared. Code only one loader can compile lives in its own package, `neoforge` or `fabric`, which the other target leaves out, and `accessoriescompatvanilla-neoforge.mixins.json` is left out of the Fabric jar with it.

## Licensing

Accessories Compat: Vanilla is licensed under the GNU Lesser General Public License, version 3 only (LGPL-3.0-only). See [LICENSE](./LICENSE), [COPYING](./COPYING) and [COPYING.LESSER](./COPYING.LESSER).

It contains code derived from Elytra Slot and Charm of Undying, Copyright (C) 2019-2022 Illusive Soulworks, licensed LGPL-3.0-or-later and used here under version 3. Please note the copyrights and trademarks in [NOTICE](./NOTICE).

## Credits

### Core Team

* aspctt - code, project lead

### Built on

* Illusive Soulworks - [Elytra Slot](https://github.com/illusivesoulworks/elytraslot) and [Charm of Undying](https://github.com/illusivesoulworks/charmofundying), which this mod ports and merges
* Wisp Forest - [Accessories](https://github.com/wisp-forest/accessories), whose slots it fills
* NeoForged - [NeoForge](https://github.com/neoforged/NeoForge), and the MDK this project started from
* FabricMC - Fabric Loader and Fabric API
