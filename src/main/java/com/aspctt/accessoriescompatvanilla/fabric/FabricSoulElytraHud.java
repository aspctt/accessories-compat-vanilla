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

package com.aspctt.accessoriescompatvanilla.fabric;

import com.aspctt.accessoriescompatvanilla.elytra.SoulElytraBoost;
import net.minecraft.ChatFormatting;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.GameType;

// Deeper Darker's Fabric boost gauge in the lower left corner, which it only draws for a Soul Elytra in the chest
// slot, along with its "Press B to boost" hint when the ground comes close.
public final class FabricSoulElytraHud {
    // Two 12 pixel columns on a 32x32 texture: the spent gauge on the left, the charged one on the right.
    private static final ResourceLocation GAUGE =
        ResourceLocation.fromNamespaceAndPath(SoulElytraBoost.MOD_ID, "textures/gui/soul_elytra_overlay_large.png");
    private static final int GAUGE_WIDTH = 12;
    private static final int GAUGE_HEIGHT = 32;
    private static final int TEXTURE_SIZE = 32;
    private static final int LEFT = 5;
    private static final int BOTTOM_OFFSET = 37;
    private static final int HINT_LEFT = 20;
    private static final int HINT_GROUND_DISTANCE = 5;

    private FabricSoulElytraHud() {
    }

    // Whether there is anything but air within a few blocks below the player.
    private static boolean isNearGround(LocalPlayer player) {
        BlockPos feet = player.getOnPos();

        for (BlockPos position : BlockPos.betweenClosed(feet, feet.below(HINT_GROUND_DISTANCE))) {
            if (!player.level().getBlockState(position).isAir()) {
                return true;
            }
        }
        return false;
    }

    public static void render(GuiGraphics graphics, DeltaTracker deltaTracker) {
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        Item soulElytra = SoulElytraBoost.getSoulElytra();

        if (player == null
            || soulElytra == null
            || minecraft.options.hideGui
            || minecraft.gameMode == null
            || minecraft.gameMode.getPlayerMode() == GameType.SPECTATOR
            || !SoulElytraBoost.isWornAsAccessory(player)
            || FabricSoulElytraBoost.getCooldown() == SoulElytraBoost.DISABLED) {
            return;
        }

        // Draw the spent part of the gauge above the charged part
        float spent = player.getCooldowns().getCooldownPercent(soulElytra, deltaTracker.getGameTimeDeltaPartialTick(false));
        int top = minecraft.getWindow().getGuiScaledHeight() - BOTTOM_OFFSET;
        int spentHeight = Mth.floor(GAUGE_HEIGHT * spent);
        int chargedHeight = Mth.ceil(GAUGE_HEIGHT * (1.0F - spent));
        graphics.blit(GAUGE, LEFT, top, 0.0F, 0.0F, GAUGE_WIDTH, spentHeight, TEXTURE_SIZE, TEXTURE_SIZE);
        graphics.blit(GAUGE, LEFT, top + spentHeight, GAUGE_WIDTH, spentHeight, GAUGE_WIDTH, chargedHeight, TEXTURE_SIZE, TEXTURE_SIZE);

        // Hint at the key once the boost is ready and the ground is close
        if (spent == 0.0F && player.isFallFlying() && isNearGround(player)) {
            Component hint = Component.translatable(
                "item.deeperdarker.soul_elytra.boost", Component.keybind(SoulElytraBoost.BOOST_KEY))
                .withStyle(ChatFormatting.YELLOW);
            graphics.drawString(minecraft.font, hint, HINT_LEFT, top, 0xFFFFFF);
        }
    }
}
