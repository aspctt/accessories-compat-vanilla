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

package com.aspctt.accessoriescompatvanilla.client;

import com.aspctt.accessoriescompatvanilla.elytra.SoulElytraBoost;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.Nullable;

// Watches Deeper Darker's boost key for a Soul Elytra worn as an accessory. The key's state is read rather than its
// clicks, since Deeper Darker's NeoForge client takes every click of it for itself.
public final class SoulElytraBoostKey {
    @Nullable
    private static KeyMapping boostKey;
    private static boolean hasSearchedForBoostKey;
    private static boolean wasBoostKeyDown;

    private SoulElytraBoostKey() {
    }

    // Deeper Darker's key mapping, found by its name. Every mod's mappings are in the options by the first tick.
    @Nullable
    private static KeyMapping getBoostKey() {
        if (!hasSearchedForBoostKey) {
            hasSearchedForBoostKey = true;

            for (KeyMapping mapping : Minecraft.getInstance().options.keyMappings) {
                if (mapping.getName().equals(SoulElytraBoost.BOOST_KEY)) {
                    boostKey = mapping;
                    break;
                }
            }
        }
        return boostKey;
    }

    // Called once every client tick. True on the tick the key goes down while the player glides on an accessory
    // Soul Elytra whose boost is ready; the server checks all of this again.
    public static boolean shouldRequestBoost() {
        KeyMapping key = getBoostKey();
        LocalPlayer player = Minecraft.getInstance().player;
        Item soulElytra = SoulElytraBoost.getSoulElytra();

        if (key == null || player == null || soulElytra == null) {
            wasBoostKeyDown = false;
            return false;
        }

        // Act on the press only, so holding the key asks once
        boolean isBoostKeyDown = key.isDown();
        boolean isPressed = isBoostKeyDown && !wasBoostKeyDown;
        wasBoostKeyDown = isBoostKeyDown;

        return isPressed
            && player.isFallFlying()
            && !player.getCooldowns().isOnCooldown(soulElytra)
            && SoulElytraBoost.isWornAsAccessory(player);
    }
}
