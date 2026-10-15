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

import com.aspctt.accessoriescompatvanilla.AccessoriesCompatVanilla;
import com.aspctt.accessoriescompatvanilla.elytra.SoulElytraBoost;
import com.aspctt.accessoriescompatvanilla.network.SoulElytraBoostPayload;
import io.wispforest.owo.config.ConfigWrapper;
import io.wispforest.owo.config.Option;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

// The Soul Elytra boost as Deeper Darker gives it on Fabric: a push along the player's look with a trail of sonic
// booms behind them.
public final class FabricSoulElytraBoost {
    private static final String CONFIG_HOLDER = "com.kyanite.deeperdarker.DeeperDarker";
    private static final Option.Key COOLDOWN_SETTING = new Option.Key("server.soulElytraCooldown");
    private static final double BOOST_STRENGTH = 1.5;
    private static final int TRAIL_LENGTH = 5;

    @Nullable
    private static Option<?> cooldownOption;
    private static boolean hasSearchedForCooldownOption;

    private FabricSoulElytraBoost() {
    }

    // Deeper Darker keeps its settings in an owo-lib config held by its main class. owo-lib has no lookup of configs
    // by name, so the config is reached through that field and the setting through owo-lib's own option lookup.
    @Nullable
    private static Option<?> getCooldownOption() {
        if (hasSearchedForCooldownOption) {
            return cooldownOption;
        }
        hasSearchedForCooldownOption = true;

        try {
            if (Class.forName(CONFIG_HOLDER).getField("CONFIG").get(null) instanceof ConfigWrapper<?> config) {
                cooldownOption = config.optionForKey(COOLDOWN_SETTING);
            }
        } catch (ReflectiveOperationException exception) {
            AccessoriesCompatVanilla.LOGGER.warn("Could not read Deeper Darker's Soul Elytra cooldown", exception);
        }
        return cooldownOption;
    }

    // Deeper Darker's cooldown setting in ticks, on whichever side asks, as Deeper Darker reads it.
    public static int getCooldown() {
        Option<?> option = getCooldownOption();
        return option != null && option.value() instanceof Integer cooldown ? cooldown : SoulElytraBoost.DEFAULT_COOLDOWN;
    }

    // Runs on the server thread.
    public static void handle(SoulElytraBoostPayload payload, ServerPlayNetworking.Context context) {
        ServerPlayer player = context.player();
        Item soulElytra = SoulElytraBoost.getSoulElytra();

        if (soulElytra == null || !SoulElytraBoost.isWornAsAccessory(player)) {
            return;
        }
        int cooldown = getCooldown();

        if (cooldown == SoulElytraBoost.DISABLED) {
            player.displayClientMessage(Component.translatable("item.deeperdarker.soul_elytra.boost_disabled")
                .withStyle(ChatFormatting.RED), true);
            return;
        }
        if (!player.isFallFlying() || player.getCooldowns().isOnCooldown(soulElytra)) {
            return;
        }

        // Push the player along their look, and tell their client at once since it moves them itself
        Vec3 look = player.getLookAngle();
        player.addDeltaMovement(look.scale(BOOST_STRENGTH));
        player.connection.send(new ClientboundSetEntityMotionPacket(player));

        // Trail sonic booms behind them
        ServerLevel level = player.serverLevel();
        Vec3 behind = look.reverse();

        for (int i = 0; i < TRAIL_LENGTH; i++) {
            Vec3 position = player.position().add(behind.scale(i));
            level.sendParticles(ParticleTypes.SONIC_BOOM, position.x, position.y, position.z, 1, 0.0, 0.0, 0.0, 0.0);
        }

        // Start the cooldown
        player.getCooldowns().addCooldown(soulElytra, cooldown);
    }
}
