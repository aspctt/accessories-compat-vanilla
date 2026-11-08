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
import io.wispforest.accessories.api.attributes.AccessoryAttributeBuilder;
import io.wispforest.accessories.api.slot.SlotReference;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;

// Flight and durability are not handled here but where each loader runs its own elytra logic, so an accessory
// elytra ticks exactly when a chest one would.
public class ElytraAccessory implements Accessory {
    public static final ElytraAccessory INSTANCE = new ElytraAccessory();

    // Apothic Attributes gives every elytra this attribute in the chest slot, and reads it in place of a chest
    // item's own flight check. Applied from the cape slot, it would let a chestplate fly instead of the elytra,
    // so the elytra would stop wearing down and a broken one would still fly. Flight from the slot is this
    // mod's own, so the attribute is left out.
    private static final ResourceLocation APOTHIC_ELYTRA_FLIGHT = ResourceLocation.parse("apothic_attributes:elytra_flight");

    // Accessories applies only its own attribute data, so the elytra's attribute modifiers are handed over here as
    // they would apply in the chest slot. On NeoForge this also takes in what mods add to the stack through
    // ItemAttributeModifierEvent, such as Apotheosis affixes, gems and bonus modifiers.
    @Override
    public void getDynamicModifiers(ItemStack stack, SlotReference reference, AccessoryAttributeBuilder builder) {
        stack.forEachModifier(EquipmentSlot.CHEST, (attribute, modifier) -> {
            if (attribute.is(APOTHIC_ELYTRA_FLIGHT)) {
                return;
            }

            // Added as stackable, so Accessories gives the modifier an ID of its own for this slot. A chestplate
            // with the same modifier would otherwise share its ID, and taking off the elytra would also remove
            // the chestplate's.
            builder.addStackable(attribute, modifier);
        });
    }

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
