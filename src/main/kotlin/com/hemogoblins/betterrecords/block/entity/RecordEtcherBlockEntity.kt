package com.hemogoblins.betterrecords.block.entity

import com.hemogoblins.betterrecords.BetterRecords
import com.hemogoblins.betterrecords.block.ModBlocks
import com.hemogoblins.betterrecords.menu.RecordEtcherMenu
import net.minecraft.core.BlockPos
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.world.MenuProvider
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.level.block.entity.BlockEntityTicker
import net.minecraft.world.level.block.entity.ChestLidController
import net.minecraft.world.level.block.state.BlockState

class RecordEtcherBlockEntity(
    pos: BlockPos,
    state: BlockState
): SingleSlotBlockEntity<RecordEtcherBlockEntity>(
        pos,
        state,
        ModBlocks.RECORD_ETCHER_ENTITY.get()
), MenuProvider {

    companion object {
        val animationTicker = BlockEntityTicker<RecordEtcherBlockEntity> { _, _, _, entity ->
            entity.needleController.tickLid()
        }
    }

    private val needleController = ChestLidController()
    fun getNeedleOpenness(partialTick: Float) = needleController.getOpenness(partialTick)
    private fun updateNeedleController() = needleController.shouldBeOpen(!isEmpty)

    override fun createMenu(windowId: Int, inventory: Inventory, player: Player): AbstractContainerMenu {
        return RecordEtcherMenu(windowId, inventory, this)
    }

    override fun getDisplayName(): Component {
        return Component.translatable("menu.${BetterRecords.ID}.record_etcher.title")
    }

    override fun load(tag: CompoundTag) {
        super.load(tag)
        updateNeedleController()
    }
    override fun triggerUpdate() {
        updateNeedleController()
        super.triggerUpdate()
    }
}
