/*
 * Copyright (C) 2026 ASPCT
 *
 * This file is part of Accessories Compat: Vanilla.
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

package com.aspctt.accessoriescompatvanilla.client;

import com.aspctt.accessoriescompatvanilla.platform.Platform;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.kikugie.elytratrims.api.ElytraTrimsAPI;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

// Elytra Trims draws its trims, banner patterns, dyes and other effects through mixins into vanilla's elytra layer
// and into the layers of the elytra slot mods it knows, Elytra Slot's among them. None of those draw the cape slot
// here, so the elytra renderer hands its elytra to Elytra Trims' API itself. Elytra Trims is only built against,
// never shipped, and its classes are only reached once it is known to be loaded.
public final class ElytraTrimsCompat {
    private static final String MOD_ID = "elytratrims";
    // 3.10.0 replaced renderFeatures with one that also takes the elytra's texture, and added shouldRenderBase.
    // Older releases lack both, so the cape slot stays without their effects there rather than crashing.
    private static final String MINIMUM_VERSION = "3.10.0";
    // Read the first time an elytra is drawn, long after the loader entry point has set Platform up.
    private static final boolean IS_LOADED = Platform.get().isModLoaded(MOD_ID, MINIMUM_VERSION);

    private ElytraTrimsCompat() {
    }

    // Whether the player's cape may be drawn on their elytra, which Elytra Trims lets players turn off.
    public static boolean shouldShowCape(LivingEntity entity) {
        if (!IS_LOADED) {
            return true;
        }
        return ElytraTrimsAPI.shouldShowCape(entity);
    }

    // Whether the elytra itself is drawn under Elytra Trims' effects. Its end gateway effect takes the elytra's
    // place rather than covering it.
    public static boolean shouldRenderBase(ItemStack stack) {
        if (!IS_LOADED) {
            return true;
        }
        return ElytraTrimsAPI.shouldRenderBase(stack);
    }

    // Draws Elytra Trims' effects over an elytra model already posed for the entity. The texture is the one the
    // elytra was drawn with, which the end gateway effect uses as its mask.
    public static void renderFeatures(
        Model model,
        PoseStack matrices,
        MultiBufferSource multiBufferSource,
        LivingEntity entity,
        ItemStack stack,
        ResourceLocation texture,
        int light
    ) {
        if (!IS_LOADED) {
            return;
        }
        // -1 is opaque white, the tint vanilla's elytra layer draws with, so the effects keep their own colours.
        ElytraTrimsAPI.renderFeatures(model, matrices, multiBufferSource, entity, stack, texture, light, -1);
    }
}
