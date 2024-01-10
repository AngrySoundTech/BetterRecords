package com.hemogoblins.betterrecords.block

import com.hemogoblins.betterrecords.block.entity.RecordEtcherBlockEntity
import com.hemogoblins.betterrecords.block.entity.RecordPlayerBlockEntity
import net.minecraft.core.BlockPos
import net.minecraft.sounds.SoundEvent
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.EntityBlock
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.BlockHitResult

class RecordPlayerBlock(properties: Properties): Block(properties), EntityBlock {

    override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity {
        return RecordPlayerBlockEntity(pos, state)
    }

    override fun use(
            state: BlockState,
            level: Level,
            pos: BlockPos,
            player: Player,
            hand: InteractionHand,
            hit: BlockHitResult
    ): InteractionResult {

        val ent = level.getBlockEntity(pos) as RecordPlayerBlockEntity
        var didInteract = false

        if (player.isCrouching) {
            // real implementation later
            when (ent.isOpen) {
                true -> level.playSound(null, pos, SoundEvents.CHEST_CLOSE, SoundSource.NEUTRAL, .2f, level.random.nextFloat() * 0.2f + 3f)
                false -> level.playSound(null, pos, SoundEvents.CHEST_OPEN, SoundSource.NEUTRAL, .2f, level.random.nextFloat() * 0.2f + 3f)
            }

            ent.toggleLid()
            didInteract = true
        } // else if (isOpen && isEmpty && playerHoldingRecord) insertRecord(); etc

        return if (didInteract) InteractionResult.sidedSuccess(level.isClientSide) else InteractionResult.FAIL
    }
}