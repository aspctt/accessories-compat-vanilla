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

package com.aspctt.accessoriescompatvanilla.platform;

import java.nio.file.Path;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

// The few things the two loaders do differently. Each loader's entry point sets its implementation before
// anything else in the mod runs.
public abstract class Platform {
    private static Platform instance;

    public static Platform get() {
        if (instance == null) {
            throw new IllegalStateException("Platform was used before the loader entry point set it");
        }
        return instance;
    }

    public static void set(Platform platform) {
        instance = platform;
    }

    public abstract boolean isModLoaded(String modId);

    public abstract Path getConfigDir();

    // Whether the stack grants elytra flight right now, decided the way the loader decides it for the chest slot.
    public abstract boolean canElytraFly(ItemStack stack, LivingEntity entity);

    // Clears the effects a Totem of Undying clears when it is used from the hand.
    public abstract void cureTotemEffects(LivingEntity entity);

    public abstract void sendToTrackingAndSelf(Entity entity, CustomPacketPayload payload);
}
