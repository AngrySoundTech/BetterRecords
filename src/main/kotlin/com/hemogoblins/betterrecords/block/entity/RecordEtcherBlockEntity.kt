package com.hemogoblins.betterrecords.block.entity

import com.hemogoblins.betterrecords.block.ModBlocks
import net.minecraft.core.BlockPos
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState

class RecordEtcherBlockEntity(pos: BlockPos, state: BlockState): BlockEntity(ModBlocks.RECORD_ETCHER_ENTITY.get(), pos, state) {

    var count = 1

    override fun serializeNBT(): CompoundTag {
        return CompoundTag().apply {
            putInt("count", count)
        }
    }

    override fun deserializeNBT(nbt: CompoundTag) {
        count = nbt.getInt("count")
    }

}