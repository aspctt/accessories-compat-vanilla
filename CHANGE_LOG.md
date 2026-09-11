# Accessories Compat: Vanilla :: Change Log
- - -

* Unreleased: 1.0.1
	+ Name the jar AccessoriesCompatVanilla-<version>+<minecraft version>-<loader>.jar, so the NeoForge and Fabric files no longer share a name

* 2026-09-11: 1.0.0
	+ First release, merging Elytra Slot and Charm of Undying into one mod built natively on the Accessories API
	+ Elytra in the cape slot: flies, wears down and hides the cape as it would in the chest slot, and is drawn with the wearer's own elytra or cape texture
	+ Totem of Undying in the charm slot: saves the wearer before a held totem would, with the full activation animation for everyone nearby, and is drawn on the chest
	+ Both mods' support for modded elytras (Deeper Darker, Mekanism, Lil' Wings and others) and totems (Biome Makeover, Netherite Extras, Totem of Infinity, Friends & Foes) carried over
	+ Not carried over yet: Deeper Darker's Soul Elytra boost, MinecraftCapes and Wavey Capes cape handling, and Fabric Waystones' Void Totem
	+ No Caelus API needed on NeoForge
	+ Totem display options in config/accessoriescompatvanilla-client.json
	+ Stonecutter build with NeoForge 1.21.1 and Fabric 1.21.1 targets, against NeoForge 21.1.250, Fabric API 0.116.17 and Accessories 1.1.0-beta.53
	+ Licensed LGPL-3.0-only
