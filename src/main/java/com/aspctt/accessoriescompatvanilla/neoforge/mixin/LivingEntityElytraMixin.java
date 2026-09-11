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

package com.aspctt.accessoriescompatvanilla.neoforge.mixin;

import com.aspctt.accessoriescompatvanilla.elytra.ElytraFlight;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

// NeoForge has no elytra event, so the chest slot lookup that keeps an entity gliding is pointed at an accessory
// elytra when the chest cannot fly. Vanilla then runs its own canElytraFly and elytraFlightTick checks on that
// stack, which also wears it down exactly as it would in the chest slot. Replaces Elytra Slot's use of Caelus.
@Mixin(LivingEntity.class)
public abstract class LivingEntityElytraMixin {
    @WrapOperation(
        method = "updateFallFlying",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;getItemBySlot(Lnet/minecraft/world/entity/EquipmentSlot;)Lnet/minecraft/world/item/ItemStack;")
    )
    private ItemStack accessoriescompatvanilla$keepGliding(LivingEntity entity, EquipmentSlot slot, Operation<ItemStack> original) {
        return ElytraFlight.substituteChest(entity, slot, original.call(entity, slot));
    }
}
