/*
 * Copyright (C) 2021 Hemogoblins
 * This file is part of BetterRecords.
 *
 * BetterRecords is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * BetterRecords is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with BetterRecords.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.hemogoblins.betterrecords.util;

import com.hemogoblins.betterrecords.BetterRecords;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;

public class RegistryUtil {

    public static ResourceLocation prefix(String path) {
        return new ResourceLocation(BetterRecords.MOD_ID, path);
    }

    public static <T extends IForgeRegistryEntry<T>> void register(
            IForgeRegistry<T> registry,
            ResourceLocation resourceLocation,
            IForgeRegistryEntry<T> entry
    ) {
        registry.register(entry.setRegistryName(resourceLocation));
    }

    public static <T extends IForgeRegistryEntry<T>> void register(
            IForgeRegistry<T> registry,
            String name,
            IForgeRegistryEntry<T> entry
    ) {
        register(registry, prefix(name), entry);
    }
}
