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

package com.aspctt.accessoriescompatvanilla.client;

import com.aspctt.accessoriescompatvanilla.elytra.ElytraProvider;
import com.aspctt.accessoriescompatvanilla.elytra.ElytraProviders;
import com.aspctt.accessoriescompatvanilla.totem.TotemProviders;
import io.wispforest.accessories.api.client.AccessoriesRendererRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

// The loader-independent half of the client entry point.
public final class AccessoriesCompatVanillaClient {
    private AccessoriesCompatVanillaClient() {
    }

    public static void init() {
        ClientConfig.load();
    }

    // Called once for every registered item. Accessories builds the renderers from these suppliers each time data
    // is reloaded, so the models are baked then rather than now.
    public static void registerRenderer(ResourceLocation id, Item item) {
        ElytraProvider elytra = ElytraProviders.find(id, item);

        if (elytra != null) {
            if (elytra.getTexture(id) != null) {
                AccessoriesRendererRegistry.registerRenderer(id, ElytraAccessoryRenderer::new);
            }
        } else if (TotemProviders.isRenderedTotem(id)) {
            AccessoriesRendererRegistry.registerRenderer(id, TotemAccessoryRenderer::new);
        }
    }
}
