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

import com.aspctt.accessoriescompatvanilla.AccessoriesCompatVanilla;
import com.aspctt.accessoriescompatvanilla.platform.Platform;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;

// The Totem of Undying's own effects, the same ones it gives when held.
public class VanillaTotemEffectProvider implements TotemEffectProvider {
    // NeoForge deprecates getItemEnchantmentLevel in favour of an ItemStack method Fabric does not have. Its
    // patched version already returns the gameplay level that method would, so it is right on both loaders.
    @SuppressWarnings("deprecation")
    @Override
    public void consume(HolderLookup.Provider lookup, ItemStack stack) {
        // Totem of Infinity lets a totem enchanted with Infinity be used without being spent.
        if (Platform.get().isModLoaded("mr_infinite_totem")) {
            int infinity = 0;

            try {
                infinity = EnchantmentHelper.getItemEnchantmentLevel(
                    lookup.lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.INFINITY), stack);
            } catch (Exception e) {
                AccessoriesCompatVanilla.LOGGER.error("Could not look up the Infinity enchantment", e);
            }

            if (infinity > 0) {
                return;
            }
        }
        stack.shrink(1);
    }

    @Override
    public boolean applyEffects(LivingEntity entity, DamageSource source, ItemStack stack) {
        entity.setHealth(1.0F);
        Platform.get().cureTotemEffects(entity);
        entity.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 900, 1));
        entity.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 100, 1));
        entity.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 800, 0));
        return true;
    }
}
