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

import com.aspctt.accessoriescompatvanilla.AccessoriesCompatVanilla;
import com.aspctt.accessoriescompatvanilla.platform.Platform;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import net.minecraft.util.Mth;

// Client-side display options, kept in config/accessoriescompatvanilla-client.json. A plain JSON file rather than
// a config library, so neither loader needs another dependency for what are four display settings.
public final class ClientConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final String FILE_NAME = AccessoriesCompatVanilla.MOD_ID + "-client.json";
    private static final double MAX_OFFSET = 100.0;

    private static ClientConfig current = new ClientConfig();

    // Whether an equipped totem is drawn on its wearer.
    public boolean renderTotem = true;
    // Moves the drawn totem, in blocks, on the same axes as Charm of Undying's xOffset, yOffset and zOffset.
    public double totemOffsetX = 0.0;
    public double totemOffsetY = 0.0;
    public double totemOffsetZ = 0.0;

    public static ClientConfig get() {
        return current;
    }

    public static void load() {
        Path file = Platform.get().getConfigDir().resolve(FILE_NAME);
        ClientConfig loaded = new ClientConfig();
        boolean readable = true;

        if (Files.exists(file)) {
            try (Reader reader = Files.newBufferedReader(file)) {
                ClientConfig read = GSON.fromJson(reader, ClientConfig.class);

                if (read != null) {
                    loaded = read;
                }
            } catch (IOException | JsonParseException e) {
                readable = false;
                AccessoriesCompatVanilla.LOGGER.error("Could not read {}, using the defaults", file, e);
            }
        }
        loaded.totemOffsetX = Mth.clamp(loaded.totemOffsetX, -MAX_OFFSET, MAX_OFFSET);
        loaded.totemOffsetY = Mth.clamp(loaded.totemOffsetY, -MAX_OFFSET, MAX_OFFSET);
        loaded.totemOffsetZ = Mth.clamp(loaded.totemOffsetZ, -MAX_OFFSET, MAX_OFFSET);
        current = loaded;

        // Written back so a new or partial file shows every option. A file that could not be read is left for
        // the player to fix rather than replaced.
        if (readable) {
            try {
                Files.createDirectories(file.getParent());

                try (Writer writer = Files.newBufferedWriter(file)) {
                    GSON.toJson(current, writer);
                }
            } catch (IOException e) {
                AccessoriesCompatVanilla.LOGGER.error("Could not write {}", file, e);
            }
        }
    }
}
