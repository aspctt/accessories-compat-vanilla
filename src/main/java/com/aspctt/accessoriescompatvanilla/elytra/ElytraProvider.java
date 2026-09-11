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

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.Nullable;

// Recognises a family of elytras by item id and says how to draw them. Matching by id rather than by stack lets
// the check run while items are still being registered.
public interface ElytraProvider {
    ResourceLocation DEFAULT_TEXTURE = ResourceLocation.withDefaultNamespace("textures/entity/elytra.png");

    boolean matches(ResourceLocation id, Item item);

    // The texture drawn on the elytra model, or null when this mod does not draw the elytra at all.
    @Nullable
    default ResourceLocation getTexture(ResourceLocation id) {
        return DEFAULT_TEXTURE;
    }

    // Whether a player's own elytra or cape texture replaces the item's texture, as it does for the vanilla elytra.
    default boolean usesPlayerTexture(ResourceLocation id) {
        return true;
    }
}
