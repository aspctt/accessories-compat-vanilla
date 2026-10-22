# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added

- Elytra Trims support: its trims, banner patterns, dyes and other effects show on an elytra in the cape slot, with Elytra Trims 3.10.0 or newer

## [1.0.2] - 2026-10-16

### Added

- Deeper Darker's Soul Elytra boosts from the cape slot, with Deeper Darker's own messages on NeoForge and its boost gauge on Fabric

### Fixed

- Alex's Mobs Continued's Tarantula Hawk Elytra not being drawn in the cape slot, and not flying from it on Fabric

## [1.0.1] - 2026-09-12

### Changed

- The NeoForge and Fabric jars end in `-neoforge` and `-fabric`, so the two no longer share a name

## 1.0.0 - 2026-09-11

First release, merging Elytra Slot and Charm of Undying into one mod built natively on the Accessories API, for NeoForge and Fabric 1.21.1. Deeper Darker's Soul Elytra boost, MinecraftCapes and Wavey Capes cape handling, and Fabric Waystones' Void Totem were not carried over.

### Added

- Elytra in the cape slot: flies, wears down and hides the cape as it would in the chest slot, and is drawn with the wearer's own elytra or cape texture
- Totem of Undying in the charm slot: saves the wearer before a held totem would, with the full activation animation for everyone nearby, and is drawn on the chest
- Both mods' support for modded elytras (Deeper Darker, Mekanism, Lil' Wings and others) and totems (Biome Makeover, Netherite Extras, Totem of Infinity, Friends & Foes)
- Flight on NeoForge without Caelus API
- Totem display options in `config/accessoriescompatvanilla-client.json`

[Unreleased]: https://github.com/aspctt/accessories-compat-vanilla/compare/1.0.2...HEAD
[1.0.2]: https://github.com/aspctt/accessories-compat-vanilla/compare/1.0.1...1.0.2
[1.0.1]: https://github.com/aspctt/accessories-compat-vanilla/releases/tag/1.0.1
