package com.hemogoblins.betterrecords.block

import com.hemogoblins.betterrecords.BetterRecords
import com.hemogoblins.betterrecords.menu.RecordEtcherMenu
import com.hemogoblins.betterrecords.block.entity.RecordEtcherBlockEntity
import net.minecraft.core.BlockPos
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.MenuProvider
import net.minecraft.world.SimpleMenuProvider
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.EntityBlock
import net.minecraft.world.level.block.RenderShape
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.VoxelShape
import net.minecraftforge.network.NetworkHooks
import java.awt.Menu

class RecordEtcherBlock(properties: Properties): Block(properties), EntityBlock {

    /* (Y-Up)
    / X1 Y1 Z1
    / X2 Y2 Z2 */
    val SHAPE: VoxelShape = Block.box(
            1.0, 0.0, 1.0,
            15.0, 14.0, 15.0
    )

    override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity {
        return RecordEtcherBlockEntity(pos, state)
    }

    override fun use(
        state: BlockState,
        level: Level,
        pos: BlockPos,
        player: Player,
        hand: InteractionHand,
        hit: BlockHitResult
    ): InteractionResult {
        println("USE")
        if (!level.isClientSide && player is ServerPlayer) {
            println("OPEN")
            player.openMenu(level.getBlockEntity(pos) as MenuProvider)
            // NetworkHooks.openScreen(player, state.getMenuProvider(level, pos))
        }

        return InteractionResult.sidedSuccess(level.isClientSide)
    }

    override fun getShape(state: BlockState, getter: BlockGetter, pos: BlockPos, context: CollisionContext): VoxelShape {
        return SHAPE
    }

    override fun getRenderShape(state: BlockState): RenderShape {
        return RenderShape.MODEL
    }
}