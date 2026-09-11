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
import io.wispforest.accessories.api.AccessoriesCapability;
import io.wispforest.accessories.api.AccessoriesContainer;
import io.wispforest.accessories.api.slot.SlotEntryReference;
import java.util.function.Predicate;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public final class ElytraFlight {
    // Typed so the call picks AccessoriesCapability's Predicate overload rather than its own predicate type.
    private static final Predicate<ItemStack> IS_ELYTRA = ElytraProviders::isElytra;

    private ElytraFlight() {
    }

    // The first equipped accessory elytra that can fly right now. Cosmetic slots never count.
    @Nullable
    public static SlotEntryReference findFlyable(LivingEntity entity) {
        AccessoriesCapability capability = AccessoriesCapability.get(entity);

        if (capability == null) {
            return null;
        }
        for (SlotEntryReference entry : capability.getEquipped(IS_ELYTRA)) {
            if (Platform.get().canElytraFly(entry.stack(), entity)) {
                return entry;
            }
        }
        return null;
    }

    // What the vanilla chest slot elytra checks should see: the chest item when it can fly on its own, and
    // otherwise an accessory elytra that can. For a loader with no elytra hook of its own.
    public static ItemStack substituteChest(LivingEntity entity, EquipmentSlot slot, ItemStack stack) {
        if (slot != EquipmentSlot.CHEST || Platform.get().canElytraFly(stack, entity)) {
            return stack;
        }
        SlotEntryReference entry = findFlyable(entity);
        return entry != null ? entry.stack() : stack;
    }

    // Whether an accessory elytra is being drawn on the entity, cosmetic or not, in which case it covers the cape
    // the way a chest slot elytra does. An elytra that is equipped but hidden, or that this mod does not draw,
    // leaves the cape alone.
    public static boolean hidesCape(LivingEntity entity) {
        AccessoriesCapability capability = AccessoriesCapability.get(entity);

        if (capability == null) {
            return false;
        }
        for (AccessoriesContainer container : capability.getContainers().values()) {
            int size = container.getAccessories().getContainerSize();

            for (int i = 0; i < size; i++) {
                if (!container.shouldRender(i)) {
                    continue;
                }
                ItemStack cosmetic = container.getCosmeticAccessories().getItem(i);
                ItemStack shown = cosmetic.isEmpty() ? container.getAccessories().getItem(i) : cosmetic;

                if (ElytraProviders.getTexture(shown) != null) {
                    return true;
                }
            }
        }
        return false;
    }
}
