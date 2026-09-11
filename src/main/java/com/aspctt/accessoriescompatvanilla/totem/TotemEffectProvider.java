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

import net.minecraft.core.HolderLookup;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

// What one kind of totem does when it saves its wearer.
public interface TotemEffectProvider {
    // Whether the totem also works against damage that bypasses invulnerability, such as the void.
    default boolean bypassesInvulnerability() {
        return false;
    }

    // Applies the totem's effects, given a copy of the stack as it was before it was used. Returns whether the
    // wearer was saved.
    boolean applyEffects(LivingEntity entity, DamageSource source, ItemStack stack);

    // Uses up the equipped stack.
    default void consume(HolderLookup.Provider lookup, ItemStack stack) {
        stack.shrink(1);
    }
}
