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
package com.hemogoblins.betterrecords.block;

import com.hemogoblins.betterrecords.BetterRecords;
import com.hemogoblins.betterrecords.item.ModItems;
import com.hemogoblins.betterrecords.lib.BlockNames;
import com.hemogoblins.betterrecords.util.RegistryUtil;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;

@Mod.EventBusSubscriber(
        modid = BetterRecords.MOD_ID,
        bus = Mod.EventBusSubscriber.Bus.MOD
)
public class ModBlocks {

    public static final Block recordPlayer = new BlockRecordPlayer(AbstractBlock.Properties.create(Material.WOOD));
    public static final Block recordEtcher = new BlockRecordEtcher(AbstractBlock.Properties.create(Material.WOOD));

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event) {
        IForgeRegistry<Block> registry = event.getRegistry();

        RegistryUtil.register(registry, BlockNames.RECORD_ETCHER, recordEtcher);
        RegistryUtil.register(registry, BlockNames.RECORD_PLAYER, recordPlayer);
    }

    @SubscribeEvent
    public static void registerItemBlocks(RegistryEvent.Register<Item> event) {
        IForgeRegistry<Item> registry = event.getRegistry();
        Item.Properties properties = ModItems.defaultProperties();

        RegistryUtil.register(registry, BlockNames.RECORD_ETCHER, new BlockItem(recordEtcher, properties));
        RegistryUtil.register(registry, BlockNames.RECORD_PLAYER, new BlockItem(recordPlayer, properties));
    }
}
