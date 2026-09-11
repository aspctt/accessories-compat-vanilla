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

import io.wispforest.accessories.api.Accessory;
import io.wispforest.accessories.api.SoundEventData;
import io.wispforest.accessories.api.slot.SlotReference;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;

// Flight and durability are not handled here but where each loader runs its own elytra logic, so an accessory
// elytra ticks exactly when a chest one would.
public class ElytraAccessory implements Accessory {
    public static final ElytraAccessory INSTANCE = new ElytraAccessory();

    @Override
    public boolean canEquip(ItemStack stack, SlotReference reference) {
        // A second elytra adds nothing while one is already worn in the chest slot.
        return !ElytraProviders.isElytra(reference.entity().getItemBySlot(EquipmentSlot.CHEST));
    }

    @Override
    public SoundEventData getEquipSound(ItemStack stack, SlotReference reference) {
        return new SoundEventData(SoundEvents.ARMOR_EQUIP_ELYTRA, 1.0F, 1.0F);
    }
}
