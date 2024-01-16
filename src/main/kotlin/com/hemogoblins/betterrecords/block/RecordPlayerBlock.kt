package com.hemogoblins.betterrecords.block

import com.hemogoblins.betterrecords.block.entity.RecordPlayerBlockEntity
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
import net.minecraft.world.level.block.BaseEntityBlock
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.HorizontalDirectionalBlock
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityTicker
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.block.state.properties.BooleanProperty
import net.minecraft.world.level.block.state.properties.DirectionProperty
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.VoxelShape

class RecordPlayerBlock(properties: Properties) : BaseEntityBlock(properties) {

    companion object {
        val FACING: DirectionProperty = HorizontalDirectionalBlock.FACING
        val OPEN: BooleanProperty = BlockStateProperties.OPEN

        private val SHAPE: VoxelShape = Block.box(
                0.5, 0.0, 0.5,
                15.5, 14.0, 15.5
        )

        fun isOpen(state: BlockState): Boolean {
            return state.getValue(OPEN)
        }
    }

    init {
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(OPEN, false))
    }

    override fun getStateForPlacement(context: BlockPlaceContext): BlockState? {
        return this.defaultBlockState()
                .setValue(FACING, context.horizontalDirection.opposite)
                .setValue(OPEN, false)
    }

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        builder.add(FACING).add(OPEN)
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
            when (isOpen(state)) {
                true -> level.playSound(null, pos, SoundEvents.CHEST_CLOSE, SoundSource.NEUTRAL, .2f, level.random.nextFloat() * 0.2f + 3f)
                false -> level.playSound(null, pos, SoundEvents.CHEST_OPEN, SoundSource.NEUTRAL, .2f, level.random.nextFloat() * 0.2f + 3f)
            }
            ent.toggleOpenState(state, !isOpen(state))
            didInteract = true
        } else if (isOpen(state)) {
            if (!ent.isEmpty) {
                ent.popOutRecord()
                didInteract = true
            } else didInteract = ent.tryInsertItem(player.getItemInHand(hand))
        }

        return if (didInteract) InteractionResult.sidedSuccess(level.isClientSide) else InteractionResult.FAIL // todo: should this be PASS?
    }

    override fun getShape(state: BlockState, getter: BlockGetter, pos: BlockPos, context: CollisionContext): VoxelShape {
        // todo: collider for lid state?
        return SHAPE
    }

    override fun <T : BlockEntity> getTicker(level: Level, state: BlockState, entityType: BlockEntityType<T>): BlockEntityTicker<T>? {
        return if (level.isClientSide) {
            createTickerHelper(entityType, ModBlocks.RECORD_PLAYER_ENTITY.get(), RecordPlayerBlockEntity.animationTicker)
        } else null
    }
}