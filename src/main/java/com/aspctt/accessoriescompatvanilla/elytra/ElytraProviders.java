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

package com.aspctt.accessoriescompatvanilla.elytra;

import com.aspctt.accessoriescompatvanilla.AccessoriesCompatVanilla;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public final class ElytraProviders {
    // Also counts as an elytra for flight. A modded elytra no provider knows can be added here by a datapack; it
    // then flies from an accessory slot but is not drawn, since its texture is unknown.
    public static final TagKey<Item> ELYTRA = TagKey.create(Registries.ITEM, AccessoriesCompatVanilla.id("elytra"));

    private static final List<ElytraProvider> PROVIDERS = new ArrayList<>();

    private ElytraProviders() {
    }

    public static void init() {
        PROVIDERS.add(new VanillaElytraProvider());
        PROVIDERS.add(new ModdedElytraProvider());
    }

    @Nullable
    public static ElytraProvider find(ResourceLocation id, Item item) {
        for (ElytraProvider provider : PROVIDERS) {
            if (provider.matches(id, item)) {
                return provider;
            }
        }
        return null;
    }

    @Nullable
    public static ElytraProvider find(ItemStack stack) {
        if (stack.isEmpty()) {
            return null;
        }
        return find(BuiltInRegistries.ITEM.getKey(stack.getItem()), stack.getItem());
    }

    public static boolean isElytra(ItemStack stack) {
        return !stack.isEmpty() && (stack.is(ELYTRA) || find(stack) != null);
    }

    // The texture this mod draws the stack with, or null when it does not draw it. Armour that doubles as an
    // elytra draws itself, as it did in Elytra Slot.
    @Nullable
    public static ResourceLocation getTexture(ItemStack stack) {
        if (stack.isEmpty() || stack.getItem() instanceof ArmorItem) {
            return null;
        }
        ResourceLocation id = BuiltInRegistries.ITEM.getKey(stack.getItem());
        ElytraProvider provider = find(id, stack.getItem());
        return provider != null ? provider.getTexture(id) : null;
    }
}
