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

package com.aspctt.accessoriescompatvanilla.client;

import com.aspctt.accessoriescompatvanilla.elytra.ElytraProvider;
import com.aspctt.accessoriescompatvanilla.elytra.ElytraProviders;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import io.wispforest.accessories.api.client.AccessoryRenderer;
import io.wispforest.accessories.api.slot.SlotReference;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ElytraModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.PlayerModelPart;
import net.minecraft.world.item.ItemStack;

// Draws an accessory elytra the way vanilla's ElytraLayer draws a chest one, including a player's own elytra or
// cape texture. Going through Accessories rather than a render layer of its own means the slot's visibility
// toggle and cosmetic slots apply to it.
public class ElytraAccessoryRenderer implements AccessoryRenderer {
    private final ElytraModel<LivingEntity> elytraModel;
    private final boolean usesArmorTexture;

    public ElytraAccessoryRenderer(boolean usesArmorTexture) {
        this.usesArmorTexture = usesArmorTexture;
        ModelPart root = usesArmorTexture
            ? createArmorTextureLayer().bakeRoot()
            : Minecraft.getInstance().getEntityModels().bakeLayer(ModelLayers.ELYTRA);
        elytraModel = new ElytraModel<>(root);
    }

    // Vanilla's elytra wings, mapped to where Alex's Mobs puts them on its armour texture. Baked straight from
    // the definition rather than registered as a model layer, since nothing else needs to look it up.
    private static LayerDefinition createArmorTextureLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        CubeDeformation deformation = new CubeDeformation(1.0F);
        root.addOrReplaceChild(
            "left_wing",
            CubeListBuilder.create().texOffs(32, 32).addBox(-10.0F, 0.0F, 0.0F, 10.0F, 20.0F, 2.0F, deformation),
            PartPose.offsetAndRotation(5.0F, 0.0F, 0.0F, (float) (Math.PI / 12), 0.0F, (float) (-Math.PI / 12))
        );
        root.addOrReplaceChild(
            "right_wing",
            CubeListBuilder.create().texOffs(32, 32).mirror().addBox(0.0F, 0.0F, 0.0F, 10.0F, 20.0F, 2.0F, deformation),
            PartPose.offsetAndRotation(-5.0F, 0.0F, 0.0F, (float) (Math.PI / 12), 0.0F, (float) (Math.PI / 12))
        );
        return LayerDefinition.create(mesh, 64, 64);
    }

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
        ResourceLocation texture = ElytraProviders.getTexture(stack);

        if (texture == null) {
            return;
        }
        LivingEntity entity = reference.entity();
        ElytraProvider provider = ElytraProviders.find(stack);
        ResourceLocation id = BuiltInRegistries.ITEM.getKey(stack.getItem());

        if (provider != null && provider.usesPlayerTexture(id) && entity instanceof AbstractClientPlayer player) {
            PlayerSkin skin = player.getSkin();

            if (skin.elytraTexture() != null) {
                texture = skin.elytraTexture();
            } else if (skin.capeTexture() != null
                && player.isModelPartShown(PlayerModelPart.CAPE)
                && ElytraTrimsCompat.shouldShowCape(player)) {
                texture = skin.capeTexture();
            }
        }

        matrices.pushPose();
        matrices.translate(0.0F, 0.0F, 0.125F);
        // What EntityModel.copyPropertiesTo copies, written out because the two models' entity types differ.
        elytraModel.attackTime = model.attackTime;
        elytraModel.riding = model.riding;
        elytraModel.young = model.young;
        elytraModel.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);

        // Elytra Trims' textures follow the vanilla elytra's layout and would land on the wrong faces of wings
        // drawn from an armour texture, so those are left plain, as Elytra Trims leaves them in the chest slot.
        if (usesArmorTexture || ElytraTrimsCompat.shouldRenderBase(stack)) {
            VertexConsumer consumer = ItemRenderer.getArmorFoilBuffer(
                multiBufferSource, RenderType.armorCutoutNoCull(texture), stack.hasFoil());
            elytraModel.renderToBuffer(matrices, consumer, light, OverlayTexture.NO_OVERLAY);
        }
        if (!usesArmorTexture) {
            ElytraTrimsCompat.renderFeatures(elytraModel, matrices, multiBufferSource, entity, stack, texture, light);
        }
        matrices.popPose();
    }
}
