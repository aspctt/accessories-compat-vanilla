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

import com.aspctt.accessoriescompatvanilla.network.UseTotemPayload;
import com.aspctt.accessoriescompatvanilla.platform.Platform;
import io.wispforest.accessories.api.AccessoriesCapability;
import io.wispforest.accessories.api.slot.SlotEntryReference;
import java.util.function.Predicate;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.gameevent.GameEvent;
import org.jetbrains.annotations.Nullable;

public final class TotemHandler {
    // Typed so the call picks AccessoriesCapability's Predicate overload rather than its own predicate type.
    private static final Predicate<ItemStack> IS_TOTEM = TotemProviders::isTotem;

    private TotemHandler() {
    }

    public record EquippedTotem(TotemEffectProvider provider, ItemStack stack) {
    }

    // The first totem equipped in an accessory slot. Cosmetic slots never count.
    @Nullable
    public static EquippedTotem findTotem(LivingEntity entity) {
        AccessoriesCapability capability = AccessoriesCapability.get(entity);

        if (capability == null) {
            return null;
        }
        SlotEntryReference entry = capability.getFirstEquipped(IS_TOTEM);

        if (entry == null) {
            return null;
        }
        TotemEffectProvider provider = TotemProviders.get(entry.stack().getItem());
        return provider != null ? new EquippedTotem(provider, entry.stack()) : null;
    }

    // Uses the totem the way a held one is used: it is spent, counted in the wearer's statistics, triggers the
    // advancement, and plays the activation for everyone nearby. Returns whether the wearer was saved.
    public static boolean useTotem(EquippedTotem totem, DamageSource source, LivingEntity entity) {
        ItemStack stack = totem.stack();

        if (stack.isEmpty()) {
            return false;
        }
        ItemStack copy = stack.copy();
        totem.provider().consume(entity.registryAccess(), stack);

        if (entity instanceof ServerPlayer player) {
            player.awardStat(Stats.ITEM_USED.get(copy.getItem()));
            CriteriaTriggers.USED_TOTEM.trigger(player, copy);
            entity.gameEvent(GameEvent.ITEM_INTERACT_FINISH);
        }

        if (totem.provider().applyEffects(entity, source, copy)) {
            // Vanilla's own totem event would show whatever totem is held, so the used one travels with the packet.
            Platform.get().sendToTrackingAndSelf(entity, new UseTotemPayload(entity.getId(), copy));
            return true;
        }
        return false;
    }
}
