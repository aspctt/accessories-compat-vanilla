/*
 * Copyright (C) 2019-2022 Illusive Soulworks
 * Copyright (C) 2026 ASPCT
 *
 * This file is part of Accessories Compat: Vanilla. It is derived from Charm of Undying by Illusive Soulworks and
 * was modified in 2026 to work with the Accessories API.
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

package com.aspctt.accessoriescompatvanilla.mixin;

import com.aspctt.accessoriescompatvanilla.totem.TotemHandler;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

// Neither loader has a hook for a totem that is not held, so the check is extended directly. An accessory totem
// is used before a held one, as in Charm of Undying.
@Mixin(LivingEntity.class)
public abstract class LivingEntityTotemMixin {
    @Unique
    private TotemHandler.EquippedTotem accessoriescompatvanilla$totem;

    @Inject(method = "checkTotemDeathProtection", at = @At("HEAD"), cancellable = true)
    private void accessoriescompatvanilla$findTotem(DamageSource source, CallbackInfoReturnable<Boolean> cir) {
        LivingEntity self = (LivingEntity) (Object) this;
        accessoriescompatvanilla$totem = TotemHandler.findTotem(self);

        // A totem that works against the void has to act before vanilla gives up on such damage.
        if (accessoriescompatvanilla$totem != null && accessoriescompatvanilla$totem.provider().bypassesInvulnerability()
            && TotemHandler.useTotem(accessoriescompatvanilla$totem, source, self)) {
            accessoriescompatvanilla$totem = null;
            cir.setReturnValue(true);
        }
    }

    @Inject(
        method = "checkTotemDeathProtection",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/InteractionHand;values()[Lnet/minecraft/world/InteractionHand;"),
        cancellable = true
    )
    private void accessoriescompatvanilla$useTotem(DamageSource source, CallbackInfoReturnable<Boolean> cir) {
        TotemHandler.EquippedTotem totem = accessoriescompatvanilla$totem;
        accessoriescompatvanilla$totem = null;

        if (totem != null && !totem.provider().bypassesInvulnerability()
            && TotemHandler.useTotem(totem, source, (LivingEntity) (Object) this)) {
            cir.setReturnValue(true);
        }
    }
}
