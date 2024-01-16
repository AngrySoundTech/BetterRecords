package com.hemogoblins.betterrecords.block.entity

import com.hemogoblins.betterrecords.BetterRecords
import com.hemogoblins.betterrecords.block.ModBlocks
import com.hemogoblins.betterrecords.menu.RecordEtcherMenu
import net.minecraft.core.BlockPos
import net.minecraft.network.chat.Component
import net.minecraft.world.MenuProvider
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.level.block.state.BlockState

class RecordEtcherBlockEntity(
    pos: BlockPos,
    state: BlockState
): SingleSlotBlockEntity<RecordEtcherBlockEntity>(
        pos,
        state,
        ModBlocks.RECORD_ETCHER_ENTITY.get()
), MenuProvider {

    override fun createMenu(windowId: Int, inventory: Inventory, player: Player): AbstractContainerMenu {
        return RecordEtcherMenu(windowId, inventory, this)
    }

    override fun getDisplayName(): Component {
        return Component.translatable("menu.${BetterRecords.ID}.record_etcher.title")
    }
}
