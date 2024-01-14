package com.hemogoblins.betterrecords.block

import com.hemogoblins.betterrecords.block.entity.RecordPlayerBlockEntity
import com.hemogoblins.betterrecords.item.ModItems
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.*
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityTicker
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.DirectionProperty
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.VoxelShape

class RecordPlayerBlock: BaseEntityBlock {

    val SHAPE: VoxelShape = Block.box(
            0.5, 0.0, 0.5,
            15.5, 14.0, 15.5
    )

    companion object {
        val FACING: DirectionProperty = HorizontalDirectionalBlock.FACING
    }

    constructor(properties: Properties) : super(properties) {
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH))
    }

    override fun getStateForPlacement(context: BlockPlaceContext): BlockState? {
        return this.defaultBlockState().setValue(FACING, context.horizontalDirection.opposite)
    }

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        builder.add(FACING)
    }

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

        // temp behaviour for working on rendering

        if (player.isCrouching) {
            when (ent.isOpen) {
                true -> level.playSound(null, pos, SoundEvents.CHEST_CLOSE, SoundSource.NEUTRAL, .2f, level.random.nextFloat() * 0.2f + 3f)
                false -> level.playSound(null, pos, SoundEvents.CHEST_OPEN, SoundSource.NEUTRAL, .2f, level.random.nextFloat() * 0.2f + 3f)
            }

            ent.toggleLid()
            didInteract = true
        } else if (ent.isOpen && player.isHolding(ModItems.RECORD.get())) {
            ent.togglePlaying()
            didInteract = true
        }

        return if (didInteract) InteractionResult.sidedSuccess(level.isClientSide) else InteractionResult.FAIL
    }

    override fun getShape(state: BlockState, getter: BlockGetter, pos: BlockPos, context: CollisionContext): VoxelShape {
        return SHAPE
    }

    override fun <T : BlockEntity> getTicker(level: Level, state: BlockState, entityType: BlockEntityType<T>): BlockEntityTicker<T>? {
        return if (level.isClientSide) {
            createTickerHelper(entityType, ModBlocks.RECORD_PLAYER_ENTITY.get(), RecordPlayerBlockEntity.Companion::lidAnimateTick)
        } else null
    }
}