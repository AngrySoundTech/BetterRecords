package com.hemogoblins.betterrecords.block

import com.hemogoblins.betterrecords.BetterRecords
import com.hemogoblins.betterrecords.item.ModItems
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import net.minecraftforge.eventbus.api.IEventBus
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.ForgeRegistries
import net.minecraftforge.registries.RegistryObject
import java.util.function.Supplier

object ModBlocks {

    val BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, BetterRecords.ID)

//    val TEST_BLOCK = registerBlock("test_block") {
//        Block(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK))
//    }

    fun register(eventBus: IEventBus) {
        BLOCKS.register(eventBus)
    }

    private fun <T> registerBlock(name: String, block: Supplier<T>): RegistryObject<T> where T : Block {
        return BLOCKS.register(name, block).apply {
            registerBlockItem(name, this)
        }
    }

    private fun <T> registerBlockItem(name: String, block: RegistryObject<T>): RegistryObject<Item> where T : Block {
        return ModItems.registerItem(name) { BlockItem(block.get(), Item.Properties())}
    }
}