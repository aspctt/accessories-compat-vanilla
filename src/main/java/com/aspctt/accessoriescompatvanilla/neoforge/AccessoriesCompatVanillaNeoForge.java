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
import com.aspctt.accessoriescompatvanilla.network.UseTotemPayload;
import com.aspctt.accessoriescompatvanilla.platform.Platform;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(AccessoriesCompatVanilla.MOD_ID)
public class AccessoriesCompatVanillaNeoForge {
    // Bumped whenever a payload's layout changes, so mismatched client and server builds refuse to connect.
    private static final String NETWORK_VERSION = "1";

    public AccessoriesCompatVanillaNeoForge(IEventBus modEventBus) {
        Platform.set(new NeoForgePlatform());
        AccessoriesCompatVanilla.init();
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::registerPayloads);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        // Every mod's items exist by now. Accessories keeps its accessories in a plain map, so they are added on
        // the main thread rather than from the parallel setup workers.
        event.enqueueWork(() -> BuiltInRegistries.ITEM.keySet().forEach(
            id -> AccessoriesCompatVanilla.registerAccessory(id, BuiltInRegistries.ITEM.get(id))));
    }

    private void registerPayloads(RegisterPayloadHandlersEvent event) {
        // Handlers run on the main thread by default.
        event.registrar(NETWORK_VERSION).playToClient(
            UseTotemPayload.TYPE, UseTotemPayload.STREAM_CODEC,
            (payload, context) -> UseTotemPayload.handleOnClient(payload));
    }
}
