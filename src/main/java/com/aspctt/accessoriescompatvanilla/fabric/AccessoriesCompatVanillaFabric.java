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

import com.aspctt.accessoriescompatvanilla.AccessoriesCompatVanilla;
import com.aspctt.accessoriescompatvanilla.network.UseTotemPayload;
import com.aspctt.accessoriescompatvanilla.platform.Platform;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.EntityElytraEvents;
import net.fabricmc.fabric.api.event.registry.RegistryEntryAddedCallback;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.core.registries.BuiltInRegistries;

public class AccessoriesCompatVanillaFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        Platform.set(new FabricPlatform());
        AccessoriesCompatVanilla.init();

        PayloadTypeRegistry.playS2C().register(UseTotemPayload.TYPE, UseTotemPayload.STREAM_CODEC);
        EntityElytraEvents.CUSTOM.register(FabricElytraFlight::useAccessoryElytra);

        // Mods initialise one after another, so the items already registered are handled now and the rest as
        // they arrive.
        BuiltInRegistries.ITEM.keySet().forEach(
            id -> AccessoriesCompatVanilla.registerAccessory(id, BuiltInRegistries.ITEM.get(id)));
        RegistryEntryAddedCallback.event(BuiltInRegistries.ITEM).register(
            (rawId, id, item) -> AccessoriesCompatVanilla.registerAccessory(id, item));
    }
}
