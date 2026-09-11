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

package com.aspctt.accessoriescompatvanilla.totem;

import com.aspctt.accessoriescompatvanilla.platform.Platform;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public final class TotemProviders {
    private static final Map<ResourceLocation, TotemEffectProvider> PROVIDERS = new ConcurrentHashMap<>();
    // Totems another mod handles itself, which are only drawn on the wearer here.
    private static final Set<ResourceLocation> RENDER_ONLY = ConcurrentHashMap.newKeySet();

    private TotemProviders() {
    }

    public static void init() {
        register(ResourceLocation.withDefaultNamespace("totem_of_undying"), new VanillaTotemEffectProvider());

        Platform platform = Platform.get();

        if (platform.isModLoaded("biomemakeover")) {
            register(ResourceLocation.parse("biomemakeover:enchanted_totem"), new BiomeMakeoverTotemEffectProvider());
        }
        if (platform.isModLoaded("netheriteextras")) {
            // The Totem of Neverdying wears down rather than being spent in one use.
            register(ResourceLocation.parse("netheriteextras:totem_of_neverdying"), new VanillaTotemEffectProvider() {
                @Override
                public void consume(HolderLookup.Provider lookup, ItemStack stack) {
                    stack.setDamageValue(stack.getDamageValue() + 1);

                    if (stack.getDamageValue() >= stack.getMaxDamage()) {
                        stack.shrink(1);
                    }
                }
            });
        }
        if (platform.isModLoaded("friendsandfoes")) {
            RENDER_ONLY.add(ResourceLocation.parse("friendsandfoes:totem_of_freezing"));
            RENDER_ONLY.add(ResourceLocation.parse("friendsandfoes:totem_of_illusion"));
        }
    }

    // Binds a totem's effects to its item id. Public so another mod can add its own totem the way Charm of
    // Undying allowed; the item also needs to be in the accessoriescompatvanilla:totem tag to be equipped.
    public static void register(ResourceLocation id, TotemEffectProvider provider) {
        PROVIDERS.put(id, provider);
    }

    @Nullable
    public static TotemEffectProvider get(Item item) {
        return PROVIDERS.get(BuiltInRegistries.ITEM.getKey(item));
    }

    public static boolean isTotem(ItemStack stack) {
        return !stack.isEmpty() && get(stack.getItem()) != null;
    }

    public static boolean isRenderedTotem(ResourceLocation id) {
        return PROVIDERS.containsKey(id) || RENDER_ONLY.contains(id);
    }
}
