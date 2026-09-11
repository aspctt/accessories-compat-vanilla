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

package com.aspctt.accessoriescompatvanilla.neoforge.mixin.client;

import com.aspctt.accessoriescompatvanilla.elytra.ElytraFlight;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.CapeLayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// Vanilla only hides the cape for a chest slot elytra. NeoForge has no event for it, which Elytra Slot got from
// Caelus; Fabric API's own event is used on Fabric.
@Mixin(CapeLayer.class)
public abstract class CapeLayerMixin {
    @Inject(
        method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/client/player/AbstractClientPlayer;FFFFFF)V",
        at = @At("HEAD"),
        cancellable = true
    )
    private void accessoriescompatvanilla$hideUnderElytra(
        PoseStack poseStack, MultiBufferSource buffer, int packedLight, AbstractClientPlayer player,
        float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw,
        float headPitch, CallbackInfo ci
    ) {
        if (ElytraFlight.hidesCape(player)) {
            ci.cancel();
        }
    }
}
