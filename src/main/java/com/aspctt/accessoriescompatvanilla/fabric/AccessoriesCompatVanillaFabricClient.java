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

package com.aspctt.accessoriescompatvanilla.fabric;

import com.aspctt.accessoriescompatvanilla.client.AccessoriesCompatVanillaClient;
import com.aspctt.accessoriescompatvanilla.client.SoulElytraBoostKey;
import com.aspctt.accessoriescompatvanilla.elytra.ElytraFlight;
import com.aspctt.accessoriescompatvanilla.elytra.SoulElytraBoost;
import com.aspctt.accessoriescompatvanilla.network.SoulElytraBoostPayload;
import com.aspctt.accessoriescompatvanilla.network.UseTotemPayload;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.rendering.v1.LivingEntityFeatureRenderEvents;
import net.fabricmc.fabric.api.event.registry.RegistryEntryAddedCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;

public class AccessoriesCompatVanillaFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        AccessoriesCompatVanillaClient.init();

        ClientPlayNetworking.registerGlobalReceiver(UseTotemPayload.TYPE,
            (payload, context) -> context.client().execute(() -> UseTotemPayload.handleOnClient(payload)));
        LivingEntityFeatureRenderEvents.ALLOW_CAPE_RENDER.register(player -> !ElytraFlight.hidesCape(player));

        BuiltInRegistries.ITEM.keySet().forEach(
            id -> AccessoriesCompatVanillaClient.registerRenderer(id, BuiltInRegistries.ITEM.get(id)));
        RegistryEntryAddedCallback.event(BuiltInRegistries.ITEM).register(
            (rawId, id, item) -> AccessoriesCompatVanillaClient.registerRenderer(id, item));

        if (FabricLoader.getInstance().isModLoaded(SoulElytraBoost.MOD_ID)) {
            ClientTickEvents.END_CLIENT_TICK.register(AccessoriesCompatVanillaFabricClient::onEndTick);
            HudRenderCallback.EVENT.register(FabricSoulElytraHud::render);
        }
    }

    private static void onEndTick(Minecraft client) {
        // A server without this mod has no use for the request.
        if (SoulElytraBoostKey.shouldRequestBoost() && ClientPlayNetworking.canSend(SoulElytraBoostPayload.TYPE)) {
            ClientPlayNetworking.send(SoulElytraBoostPayload.INSTANCE);
        }
    }
}
