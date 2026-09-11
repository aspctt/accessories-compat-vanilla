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

package com.aspctt.accessoriescompatvanilla;

import com.aspctt.accessoriescompatvanilla.elytra.ElytraAccessory;
import com.aspctt.accessoriescompatvanilla.elytra.ElytraProviders;
import com.aspctt.accessoriescompatvanilla.totem.TotemProviders;
import com.mojang.logging.LogUtils;
import io.wispforest.accessories.api.AccessoriesAPI;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import org.slf4j.Logger;

// The loader-independent half of the entry point. Each loader's own entry point sets up Platform first and then
// calls into here.
public final class AccessoriesCompatVanilla {
    // Must match mod_id in gradle.properties.
    public static final String MOD_ID = "accessoriescompatvanilla";
    public static final Logger LOGGER = LogUtils.getLogger();

    private AccessoriesCompatVanilla() {
    }

    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }

    public static void init() {
        ElytraProviders.init();
        TotemProviders.init();
    }

    // Called once for every registered item, so it stays cheap for the ones this mod does not handle. Totems need
    // no accessory of their own: the charm slot tag is enough for Accessories to accept them.
    public static void registerAccessory(ResourceLocation id, Item item) {
        if (ElytraProviders.find(id, item) != null) {
            AccessoriesAPI.registerAccessory(item, ElytraAccessory.INSTANCE);
        }
    }
}
