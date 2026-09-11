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
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

// The check that starts a glide, run on the client when jumping and again on the server when it hears of it. Only
// the chest slot lookup is replaced, so anything else that decides when a glide may start, such as Do a Barrel
// Roll's jump activation, still applies.
@Mixin(Player.class)
public abstract class PlayerElytraMixin {
    @WrapOperation(
        method = "tryToStartFallFlying",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;getItemBySlot(Lnet/minecraft/world/entity/EquipmentSlot;)Lnet/minecraft/world/item/ItemStack;")
    )
    private ItemStack accessoriescompatvanilla$startGliding(Player player, EquipmentSlot slot, Operation<ItemStack> original) {
        return ElytraFlight.substituteChest(player, slot, original.call(player, slot));
    }
}
