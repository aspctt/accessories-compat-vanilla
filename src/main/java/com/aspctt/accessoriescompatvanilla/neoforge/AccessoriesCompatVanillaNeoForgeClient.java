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

package com.aspctt.accessoriescompatvanilla.neoforge;

import com.aspctt.accessoriescompatvanilla.AccessoriesCompatVanilla;
import com.aspctt.accessoriescompatvanilla.client.AccessoriesCompatVanillaClient;
import com.aspctt.accessoriescompatvanilla.client.SoulElytraBoostKey;
import com.aspctt.accessoriescompatvanilla.elytra.SoulElytraBoost;
import com.aspctt.accessoriescompatvanilla.network.SoulElytraBoostPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.network.PacketDistributor;

// This class will not load on dedicated servers. Accessing client side code from here is safe.
@Mod(value = AccessoriesCompatVanilla.MOD_ID, dist = Dist.CLIENT)
public class AccessoriesCompatVanillaNeoForgeClient {
    public AccessoriesCompatVanillaNeoForgeClient(IEventBus modEventBus) {
        modEventBus.addListener(this::clientSetup);

        // Asked of NeoForge directly, since this may be built before the common entry point sets up Platform.
        if (ModList.get().isLoaded(SoulElytraBoost.MOD_ID)) {
            NeoForge.EVENT_BUS.addListener(this::onClientTick);
        }
    }

    private void clientSetup(FMLClientSetupEvent event) {
        // Accessories builds its renderers when data is reloaded on joining a world, well after this runs.
        event.enqueueWork(() -> {
            AccessoriesCompatVanillaClient.init();
            BuiltInRegistries.ITEM.keySet().forEach(
                id -> AccessoriesCompatVanillaClient.registerRenderer(id, BuiltInRegistries.ITEM.get(id)));
        });
    }

    private void onClientTick(ClientTickEvent.Post event) {
        LocalPlayer player = Minecraft.getInstance().player;

        if (SoulElytraBoostKey.shouldRequestBoost()) {
            PacketDistributor.sendToServer(SoulElytraBoostPayload.INSTANCE);
        }
        if (player != null) {
            NeoForgeSoulElytraBoost.showCooldown(player);
        }
    }
}
