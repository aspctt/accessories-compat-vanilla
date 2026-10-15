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

package com.aspctt.accessoriescompatvanilla.neoforge;

import com.aspctt.accessoriescompatvanilla.elytra.SoulElytraBoost;
import com.aspctt.accessoriescompatvanilla.network.SoulElytraBoostPayload;
import io.wispforest.accessories.api.events.SlotStateChange;
import io.wispforest.accessories.api.slot.SlotReference;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.config.ModConfigs;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.network.handling.IPayloadContext;

// The Soul Elytra boost as Deeper Darker gives it on NeoForge: a firework rocket, a "Press B to boost" message when
// the elytra goes on and a countdown while it recharges.
public final class NeoForgeSoulElytraBoost {
    private static final String COOLDOWN_SETTING = "soulElytraCooldown";

    private NeoForgeSoulElytraBoost() {
    }

    // Deeper Darker's cooldown setting in ticks, read from its common config on whichever side asks, as Deeper
    // Darker reads it. The lookup goes through NeoForge rather than Deeper Darker's config class, which moved the
    // setting from a static field to an instance in 1.4.
    public static int getCooldown() {
        for (ModConfig config : ModConfigs.getModConfigs(SoulElytraBoost.MOD_ID)) {
            if (config.getSpec() instanceof ModConfigSpec spec
                && spec.isLoaded()
                && spec.getValues().get(COOLDOWN_SETTING) instanceof ModConfigSpec.IntValue cooldown) {
                return cooldown.get();
            }
        }
        return SoulElytraBoost.DEFAULT_COOLDOWN;
    }

    // Runs on the main thread.
    public static void handle(SoulElytraBoostPayload payload, IPayloadContext context) {
        Player player = context.player();
        Item soulElytra = SoulElytraBoost.getSoulElytra();
        int cooldown = getCooldown();

        // Deeper Darker's client asks for a boost on every press of the key, so its own server has already said
        // when the boost is disabled.
        if (soulElytra == null
            || cooldown == SoulElytraBoost.DISABLED
            || !player.isFallFlying()
            || player.getCooldowns().isOnCooldown(soulElytra)
            || !SoulElytraBoost.isWornAsAccessory(player)) {
            return;
        }

        // Launch a rocket that carries the player, then start the cooldown
        Level level = player.level();
        level.addFreshEntity(new FireworkRocketEntity(level, new ItemStack(Items.FIREWORK_ROCKET), player));
        player.getCooldowns().addCooldown(soulElytra, cooldown);
    }

    // Accessories only reports changes to the real slots, and only on the server.
    public static void onAccessoryChange(
        ItemStack previousStack,
        ItemStack currentStack,
        SlotReference reference,
        SlotStateChange stateChange
    ) {
        Item soulElytra = SoulElytraBoost.getSoulElytra();

        if (soulElytra == null
            || !currentStack.is(soulElytra)
            || previousStack.is(soulElytra)
            || !(reference.entity() instanceof ServerPlayer player)
            || getCooldown() == SoulElytraBoost.DISABLED) {
            return;
        }
        // A keybind component shows the key the player has bound, resolved on their side.
        player.displayClientMessage(Component.translatable(
            "item.deeperdarker.soul_elytra.equipped", Component.keybind(SoulElytraBoost.BOOST_KEY)), true);
    }

    // Called every client tick with the local player.
    public static void showCooldown(Player player) {
        Item soulElytra = SoulElytraBoost.getSoulElytra();

        if (soulElytra == null
            || !player.getCooldowns().isOnCooldown(soulElytra)
            || !SoulElytraBoost.isWornAsAccessory(player)) {
            return;
        }
        float remaining = player.getCooldowns().getCooldownPercent(soulElytra, 0.0F);
        int seconds = Mth.ceil(remaining * getCooldown() / 20.0F);
        player.displayClientMessage(Component.translatable("item.deeperdarker.soul_elytra.cooldown", seconds), true);
    }
}
