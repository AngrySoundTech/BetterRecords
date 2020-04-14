/**
 * The MIT License
 *
 * Copyright (c) 2019 Nicholas Feldman
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package tech.feldman.betterrecords.block

import tech.feldman.betterrecords.ID
import tech.feldman.betterrecords.block.itemblock.ItemBlockSpeaker
import net.minecraft.block.Block
import net.minecraft.item.Item
import net.minecraft.item.ItemBlock
import net.minecraftforge.client.event.ModelRegistryEvent
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

@Mod.EventBusSubscriber(modid = ID)
object ModBlocks {

    val blockRecordEtcher = BlockRecordEtcher("recordetcher")
    val blockRecordPlayer = BlockRecordPlayer("recordplayer")
    val blockFrequencyTuner = BlockFrequencyTuner("frequencytuner")
    val blockRadio = BlockRadio("radio")
    val blockSpeaker = BlockSpeaker("speaker")
    val blockStrobeLight = BlockStrobeLight("strobelight")
    val blockLaser = BlockLaser("laser")
    val blockLaserCluster = BlockLaserCluster("lasercluster")

    @SubscribeEvent
    fun registerBlocks(event: RegistryEvent.Register<Block>) {
        event.registry.registerAll(
                blockRecordEtcher,
                blockRecordPlayer,
                blockFrequencyTuner,
                blockRadio,
                blockSpeaker,
                blockStrobeLight,
                blockLaser,
                blockLaserCluster
        )
    }

    @SubscribeEvent
    fun registerItems(event: RegistryEvent.Register<Item>) {
        arrayOf<ModBlock>(
                blockRecordEtcher,
                blockRecordPlayer,
                blockFrequencyTuner,
                blockRadio,
                blockStrobeLight,
                blockLaser,
                blockLaserCluster
        ).map {
            ItemBlock(it).setRegistryName(it.registryName)
        }.forEach(event.registry::register)

        // Register the speaker specially since it has variants
        event.registry.register(ItemBlockSpeaker(blockSpeaker).setRegistryName(blockSpeaker.registryName))

        Block.REGISTRY
                .filterIsInstance<ModBlock>()
                .forEach {
                    if (it is TileEntityProvider<*>) {
                        it.registerTileEntity(it)
                    }
                }
    }

    @SubscribeEvent
    fun registerModels(event: ModelRegistryEvent) {
        Block.REGISTRY
                .filterIsInstance<ModBlock>()
                .forEach {
                    it.setStateMapper()

                    if (it is ItemModelProvider) {
                        it.registerItemModel(it)
                    }

                    if (it is TESRProvider<*>) {
                        it.bindTESR()
                        it.registerTESRItemStacks(it)
                    }
                }
    }
}
