package com.hemogoblins.betterrecords.block

import com.hemogoblins.betterrecords.BetterRecords
import com.hemogoblins.betterrecords.block.entity.RecordEtcherBlockEntity
import com.hemogoblins.betterrecords.block.entity.RecordPlayerBlockEntity
import com.hemogoblins.betterrecords.item.ModItems
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockBehaviour
import net.minecraftforge.eventbus.api.IEventBus
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.ForgeRegistries
import net.minecraftforge.registries.RegistryObject
import java.util.function.Supplier
import kotlin.reflect.full.primaryConstructor

object ModBlocks {

    val BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, BetterRecords.ID)
    val BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, BetterRecords.ID)

    val RECORD_ETCHER = registerBlock("record_etcher") {
        RecordEtcherBlock(BlockBehaviour.Properties.copy(Blocks.JUKEBOX).noOcclusion())
    }

    val RECORD_PLAYER = registerBlock("record_player") {
        RecordPlayerBlock(BlockBehaviour.Properties.copy(Blocks.JUKEBOX).noOcclusion())
    }

    val RECORD_ETCHER_ENTITY = BLOCK_ENTITIES.register("record_etcher") {
        BlockEntityType.Builder.of(::RecordEtcherBlockEntity, RECORD_ETCHER.get()).build(null)
    }

    val RECORD_PLAYER_ENTITY = BLOCK_ENTITIES.register("record_player") {
        BlockEntityType.Builder.of(::RecordPlayerBlockEntity, RECORD_PLAYER.get()).build(null)
    }

    fun register(eventBus: IEventBus) {
        BLOCKS.register(eventBus)
        BLOCK_ENTITIES.register(eventBus)
    }

    private fun <T> registerBlock(name: String, block: Supplier<T>): RegistryObject<T> where T : Block {
        return BLOCKS.register(name, block).apply {
            registerBlockItem(name, this)
        }
    }

    private fun <T> registerBlockItem(name: String, block: RegistryObject<T>): RegistryObject<Item> where T : Block {
        return ModItems.registerItem(name) { BlockItem(block.get(), Item.Properties())}
    }

    private fun <T> registerBlockEntity(name: String, blockEntity: Supplier<T>): RegistryObject<T> where T : BlockEntityType<T> {
        return BLOCK_ENTITIES.register(name, blockEntity)
    }
}