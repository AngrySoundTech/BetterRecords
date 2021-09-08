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
package com.hemogoblins.betterrecords.block.tile;

import com.hemogoblins.betterrecords.BetterRecords;
import com.hemogoblins.betterrecords.block.ModBlocks;
import com.hemogoblins.betterrecords.lib.BlockNames;
import com.hemogoblins.betterrecords.util.RegistryUtil;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;

@Mod.EventBusSubscriber(
        modid = BetterRecords.MOD_ID,
        bus = Mod.EventBusSubscriber.Bus.MOD
)
public class ModTiles {

    public static final TileEntityType<TileRecordPlayer> recordPlayer = TileEntityType.Builder.create(
            TileRecordPlayer::new,
            ModBlocks.recordPlayer
    ).build(null);

    public static final TileEntityType<TileRecordEtcher> recordEtcher = TileEntityType.Builder.create(
            TileRecordEtcher::new,
            ModBlocks.recordEtcher
    ).build(null);

    @SubscribeEvent
    public static void registerTiles(RegistryEvent.Register<TileEntityType<?>> event) {
        IForgeRegistry<TileEntityType<?>> registry = event.getRegistry();

        RegistryUtil.register(registry, BlockNames.RECORD_PLAYER, recordPlayer);
        RegistryUtil.register(registry, BlockNames.RECORD_ETCHER, recordEtcher);
    }
}
