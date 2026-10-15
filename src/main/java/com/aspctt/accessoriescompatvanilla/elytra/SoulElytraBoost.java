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

import io.wispforest.accessories.api.AccessoriesCapability;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.Nullable;

// Deeper Darker only boosts a Soul Elytra in the chest slot, so for one worn as an accessory the client asks for the
// boost with a payload of its own and each loader gives it the way Deeper Darker does there. Nothing here touches
// Deeper Darker's classes: its settings moved between releases, and looking things up by name keeps every release
// working.
public final class SoulElytraBoost {
    public static final String MOD_ID = "deeperdarker";
    public static final String BOOST_KEY = "key.deeperdarker.boost";
    // Deeper Darker's own default, used when its setting cannot be read.
    public static final int DEFAULT_COOLDOWN = 600;
    // The cooldown setting that turns the boost off.
    public static final int DISABLED = -1;

    private static final ResourceLocation SOUL_ELYTRA = ResourceLocation.fromNamespaceAndPath(MOD_ID, "soul_elytra");

    private SoulElytraBoost() {
    }

    // The Soul Elytra item, or null without Deeper Darker. Its boost cooldown is kept per item, as Deeper Darker
    // keeps it.
    @Nullable
    public static Item getSoulElytra() {
        return BuiltInRegistries.ITEM.getOptional(SOUL_ELYTRA).orElse(null);
    }

    // Whether the entity wears a Soul Elytra in an accessory slot. One in the chest slot is left to Deeper Darker,
    // and cosmetic slots never count.
    public static boolean isWornAsAccessory(LivingEntity entity) {
        Item soulElytra = getSoulElytra();

        if (soulElytra == null || entity.getItemBySlot(EquipmentSlot.CHEST).is(soulElytra)) {
            return false;
        }
        AccessoriesCapability capability = AccessoriesCapability.get(entity);
        return capability != null && capability.isEquipped(soulElytra);
    }
}
