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

package com.aspctt.accessoriescompatvanilla.network;

import com.aspctt.accessoriescompatvanilla.AccessoriesCompatVanilla;
import com.aspctt.accessoriescompatvanilla.client.ClientTotemHandler;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.item.ItemStack;

// Sent to everyone tracking an entity that an accessory totem just saved, so they see the totem activation.
public record UseTotemPayload(int entityId, ItemStack stack) implements CustomPacketPayload {
    public static final Type<UseTotemPayload> TYPE = new Type<>(AccessoriesCompatVanilla.id("use_totem"));
    public static final StreamCodec<RegistryFriendlyByteBuf, UseTotemPayload> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.VAR_INT, UseTotemPayload::entityId,
        ItemStack.STREAM_CODEC, UseTotemPayload::stack,
        UseTotemPayload::new
    );

    // The client class is only reached from here, so a dedicated server never loads it.
    public static void handleOnClient(UseTotemPayload payload) {
        ClientTotemHandler.handle(payload);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
