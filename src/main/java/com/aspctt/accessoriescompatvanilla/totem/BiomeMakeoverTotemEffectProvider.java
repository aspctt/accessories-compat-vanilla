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
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

// Biome Makeover's Enchanted Totem, which heals more and protects for longer than the vanilla one.
public class BiomeMakeoverTotemEffectProvider implements TotemEffectProvider {
    @Override
    public boolean applyEffects(LivingEntity entity, DamageSource source, ItemStack stack) {
        entity.setHealth(entity.getMaxHealth() / 2.0F);
        Platform.get().cureTotemEffects(entity);
        entity.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 500, 1));
        entity.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 1200, 3));
        entity.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 2000, 0));
        entity.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 2000, 0));
        return true;
    }
}
