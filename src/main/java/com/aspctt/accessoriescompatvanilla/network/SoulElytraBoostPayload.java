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

package com.aspctt.accessoriescompatvanilla.network;

import com.aspctt.accessoriescompatvanilla.AccessoriesCompatVanilla;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

// Sent by a player who pressed Deeper Darker's boost key while gliding on a Soul Elytra worn as an accessory.
public record SoulElytraBoostPayload() implements CustomPacketPayload {
    public static final SoulElytraBoostPayload INSTANCE = new SoulElytraBoostPayload();
    public static final Type<SoulElytraBoostPayload> TYPE =
        new Type<>(AccessoriesCompatVanilla.id("soul_elytra_boost"));
    public static final StreamCodec<RegistryFriendlyByteBuf, SoulElytraBoostPayload> STREAM_CODEC =
        StreamCodec.unit(INSTANCE);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
