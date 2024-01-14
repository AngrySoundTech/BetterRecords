package com.hemogoblins.betterrecords.block.entity

import com.hemogoblins.betterrecords.block.ModBlocks
import com.hemogoblins.betterrecords.item.ModItems
import net.minecraft.core.BlockPos
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.ChestLidController
import net.minecraft.world.level.block.state.BlockState

class RecordPlayerBlockEntity(pos: BlockPos, state: BlockState): BlockEntity(ModBlocks.RECORD_PLAYER_ENTITY.get(), pos, state) {

    companion object {
        fun lidAnimateTick(level: Level, blockPos: BlockPos, blockState: BlockState, entity: RecordPlayerBlockEntity) {
            entity.lidController.tickLid()
            entity.armController.tickLid()
        }
    }

    val lidController = ChestLidController()
    val armController = ChestLidController()

    var isOpen = false
    var isPlaying = false

    fun toggleLid() {
        isOpen = !isOpen
        lidController.shouldBeOpen(isOpen)
    }

    fun togglePlaying() {
        isPlaying = !isPlaying
        armController.shouldBeOpen(isPlaying)
    }

    fun getSlottedRecord(): ItemStack {
        // TODO: Get the actual item once the inventory is implemented
        return ItemStack(ModItems.RECORD.get())
    }

    // note: this nbt stuff does not save the state between world loads

    override fun serializeNBT(): CompoundTag {
        return CompoundTag().apply {
            putBoolean("isOpen", isOpen)
        }
    }

    override fun deserializeNBT(nbt: CompoundTag) {
        isOpen = nbt.getBoolean("isOpen")
    }
}