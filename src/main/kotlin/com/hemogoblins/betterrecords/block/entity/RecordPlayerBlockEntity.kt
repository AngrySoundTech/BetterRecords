package com.hemogoblins.betterrecords.block.entity

import com.hemogoblins.betterrecords.block.ModBlocks
import net.minecraft.core.BlockPos
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState

class RecordPlayerBlockEntity(pos: BlockPos, state: BlockState): BlockEntity(ModBlocks.RECORD_PLAYER_ENTITY.get(), pos, state) {
    var isOpen = false

    fun toggleLid() {
        isOpen = !isOpen
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