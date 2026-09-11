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

package com.aspctt.accessoriescompatvanilla.client;

import com.mojang.blaze3d.vertex.PoseStack;
import io.wispforest.accessories.api.client.AccessoryRenderer;
import io.wispforest.accessories.api.slot.SlotReference;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

// Hangs an equipped totem on the upper chest, where Charm of Undying drew it.
public class TotemAccessoryRenderer implements AccessoryRenderer {
    @Override
    public <M extends LivingEntity> void render(
        ItemStack stack,
        SlotReference reference,
        PoseStack matrices,
        EntityModel<M> model,
        MultiBufferSource multiBufferSource,
        int light,
        float limbSwing,
        float limbSwingAmount,
        float partialTicks,
        float ageInTicks,
        float netHeadYaw,
        float headPitch
    ) {
        ClientConfig config = ClientConfig.get();

        if (!config.renderTotem || !(model instanceof HumanoidModel<?> humanoidModel)) {
            return;
        }
        // To the top of the front of the body, following its pose when crouching. Accessories leaves the pose in
        // half-block units, turned so that +y is up and +z faces out from the chest.
        AccessoryRenderer.transformToModelPart(matrices, humanoidModel.body, 0, 1, 1);
        // Charm of Undying placed the totem 0.2 blocks below the neck and just proud of the chest, and read its
        // offsets as model space blocks, where +y is down and +z is into the back. Both converted to this frame.
        matrices.translate(
            2.0 * config.totemOffsetX,
            -0.4 - 2.0 * config.totemOffsetY,
            0.07 - 2.0 * config.totemOffsetZ
        );
        // 0.35 blocks, as in Charm of Undying.
        matrices.scale(0.7F, 0.7F, 0.7F);
        Minecraft.getInstance().getItemRenderer().renderStatic(
            stack, ItemDisplayContext.NONE, light, OverlayTexture.NO_OVERLAY, matrices, multiBufferSource,
            reference.entity().level(), 0);
    }
}
