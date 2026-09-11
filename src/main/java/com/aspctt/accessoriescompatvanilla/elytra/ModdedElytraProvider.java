/*
 * Copyright (C) 2019-2022 Illusive Soulworks
 * Copyright (C) 2026 ASPCT
 *
 * This file is part of Accessories Compat: Vanilla. It is derived from Elytra Slot by Illusive Soulworks and was
 * modified in 2026 to work with the Accessories API.
 *
 * Accessories Compat: Vanilla is free software: you can redistribute it and/or modify it under the terms of
 * the GNU Lesser General Public License as published by the Free Software Foundation, version 3.
 *
 * Accessories Compat: Vanilla is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License and the GNU Lesser General Public License
 * along with Accessories Compat: Vanilla. If not, see <https://www.gnu.org/licenses/>.
 */

package com.aspctt.accessoriescompatvanilla.elytra;

import com.aspctt.accessoriescompatvanilla.platform.Platform;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.Nullable;

// Other mods' elytras that use the vanilla elytra model with a texture of their own. Only mods that are loaded
// are listed, so the map is built once when the mod starts.
public class ModdedElytraProvider implements ElytraProvider {
    private static final ResourceLocation SOUL_ELYTRA = ResourceLocation.parse("deeperdarker:soul_elytra");

    // A null texture marks an elytra that flies from an accessory slot but that this mod does not draw.
    private final Map<ResourceLocation, ResourceLocation> textures = new HashMap<>();
    private final boolean lilWings;

    public ModdedElytraProvider() {
        Platform platform = Platform.get();

        if (platform.isModLoaded("deeperdarker")) {
            put("deeperdarker:soul_elytra", "deeperdarker:textures/entity/soul_elytra.png");
        }
        if (platform.isModLoaded("enderitemod")) {
            put("enderitemod:enderite_elytra_seperated", "minecraft:textures/entity/enderite_elytra.png");
        }
        if (platform.isModLoaded("mythicmetals")) {
            put("mythicmetals:celestium_elytra", "mythicmetals:textures/models/celestium_elytra.png");
        }
        if (platform.isModLoaded("mekanism")) {
            put("mekanism:hdpe_elytra", "mekanism:textures/entity/hdpe_elytra.png");
        }
        if (platform.isModLoaded("alexsmobs")) {
            // Elytra Slot listed this one without a texture, so it is left for Alex's Mobs to draw.
            textures.put(ResourceLocation.parse("alexsmobs:tarantula_hawk_elytra"), null);
        }
        if (platform.isModLoaded("mna")) {
            put("mna:spectral_elytra", "mna:textures/entity/elytra.png");
        }
        if (platform.isModLoaded("wooden_elytra")) {
            put("wooden_elytra:wooden_elytra", "wooden_elytra:textures/entities/wooden_elytra.png");
        }
        if (platform.isModLoaded("lolenderite")) {
            put("lolenderite:enderite_plated_elytra", "lolenderite:textures/entity/enderite_plated_elytra.png");
        }
        if (platform.isModLoaded("netherelytra")) {
            put("netherelytra:netherite_elytra", "netherelytra:textures/entity/netherite_elytra.png");
        }
        if (platform.isModLoaded("crystalmod")) {
            put("crystalmod:sapphire_elytra", "crystalmod:textures/entity/sapphire_elytra.png");
            put("crystalmod:black_tourmaline_elytra", "crystalmod:textures/entity/black_tourmaline_elytra.png");
        }
        // Lil' Wings adds one elytra per butterfly, each named after it, so they are matched by pattern rather
        // than listed.
        lilWings = platform.isModLoaded("lilwings");
    }

    private void put(String item, String texture) {
        textures.put(ResourceLocation.parse(item), ResourceLocation.parse(texture));
    }

    private boolean isLilWings(ResourceLocation id) {
        return lilWings && id.getNamespace().equals("lilwings") && id.getPath().endsWith("_elytra");
    }

    @Override
    public boolean matches(ResourceLocation id, Item item) {
        return textures.containsKey(id) || isLilWings(id);
    }

    @Nullable
    @Override
    public ResourceLocation getTexture(ResourceLocation id) {
        if (isLilWings(id)) {
            return ResourceLocation.fromNamespaceAndPath("lilwings", "textures/elytra/" + id.getPath() + ".png");
        }
        return textures.get(id);
    }

    @Override
    public boolean usesPlayerTexture(ResourceLocation id) {
        // Both are drawn as creatures' wings, which a player's cape texture would cover up.
        return !id.equals(SOUL_ELYTRA) && !isLilWings(id);
    }
}
