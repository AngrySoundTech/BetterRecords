package com.hemogoblins.betterrecords.block.entity

import com.hemogoblins.betterrecords.BetterRecords
import com.hemogoblins.betterrecords.block.ModBlocks
import com.hemogoblins.betterrecords.item.ModItems
import com.hemogoblins.betterrecords.menu.RecordEtcherMenu
import net.minecraft.core.BlockPos
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.MenuProvider
import net.minecraft.world.SimpleContainer
import net.minecraft.world.item.ItemStack

class RecordEtcherBlockEntity(
    pos: BlockPos,
    state: BlockState
): BlockEntity(ModBlocks.RECORD_ETCHER_ENTITY.get(), pos, state), MenuProvider {

    val container = SimpleContainer(1)
    override fun createMenu(windowId: Int, inventory: Inventory, player: Player): AbstractContainerMenu {
        return RecordEtcherMenu(windowId, inventory, container)
    }

    override fun getDisplayName(): Component {
        return Component.translatable("menu.${BetterRecords.ID}.record_etcher.title")
    }
    fun getSlottedRecord(): ItemStack {
        // TODO: Get the actual item once the inventory is implemented
        return ItemStack(ModItems.RECORD.get())
    }
}
