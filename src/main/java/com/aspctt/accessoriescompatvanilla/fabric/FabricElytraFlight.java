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

import com.aspctt.accessoriescompatvanilla.elytra.ElytraFlight;
import io.wispforest.accessories.api.AccessoriesAPI;
import io.wispforest.accessories.api.slot.SlotEntryReference;
import net.fabricmc.fabric.api.entity.event.v1.FabricElytraItem;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.gameevent.GameEvent;

public final class FabricElytraFlight {
    private FabricElytraFlight() {
    }

    // Fabric API asks this whenever the chest slot cannot fly: to start a glide, and every tick to keep one going,
    // with tickElytra set when the elytra should also wear down.
    public static boolean useAccessoryElytra(LivingEntity entity, boolean tickElytra) {
        SlotEntryReference entry = ElytraFlight.findFlyable(entity);

        if (entry == null) {
            return false;
        }
        if (tickElytra) {
            ItemStack stack = entry.stack();

            if (stack.getItem() instanceof FabricElytraItem fabricElytraItem) {
                return fabricElytraItem.useCustomElytra(entity, stack, true);
            }
            wearDown(entity, entry);
        }
        return true;
    }

    // Vanilla's chest slot elytra wear, which Fabric API does not apply to an elytra outside the chest slot. A
    // stack that breaks is reported to Accessories rather than as a chest slot item.
    private static void wearDown(LivingEntity entity, SlotEntryReference entry) {
        int nextFlightTick = entity.getFallFlyingTicks() + 1;

        if (entity.level() instanceof ServerLevel level && nextFlightTick % 10 == 0) {
            if ((nextFlightTick / 10) % 2 == 0) {
                entry.stack().hurtAndBreak(1, level, entity instanceof ServerPlayer player ? player : null,
                    item -> AccessoriesAPI.breakStack(entry.reference()));
            }
            entity.gameEvent(GameEvent.ELYTRA_GLIDE);
        }
    }
}
