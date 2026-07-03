package com.hemogoblins.betterrecords.block.entity

import com.hemogoblins.betterrecords.block.ModBlocks
import com.hemogoblins.betterrecords.block.RecordPlayerBlock
import com.hemogoblins.betterrecords.block.RecordPlayerBlock.Companion.FACING
import com.hemogoblins.betterrecords.network.ModNetwork
import com.hemogoblins.betterrecords.network.clientbound.PlayRecordPacket
import com.hemogoblins.betterrecords.network.clientbound.StopRecordPacket
import net.minecraft.core.BlockPos
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.level.block.entity.BlockEntityTicker
import net.minecraft.world.level.block.entity.ChestLidController
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.Vec3
import net.minecraftforge.network.PacketDistributor
import thedarkcolour.kotlinforforge.forge.vectorutil.v3d.toVec3

class RecordPlayerBlockEntity(
        pos: BlockPos,
        state: BlockState
): SingleSlotBlockEntity<RecordPlayerBlockEntity>(
        pos,
        state,
        ModBlocks.RECORD_PLAYER_ENTITY.get()
) {

    companion object {
        const val NBT_TAG_PLAYING = "isPlaying"

        val animationTicker = BlockEntityTicker<RecordPlayerBlockEntity> { _, _, _, entity ->
            entity.lidController.tickLid()
            entity.armController.tickLid()
        }
    }

    private val lidController = ChestLidController()
    private val armController = ChestLidController()
    fun getLidOpenness(partialTick: Float) = lidController.getOpenness(partialTick)
    fun getArmOpenness(partialTick: Float) = armController.getOpenness(partialTick)

    var isPlaying = false

    fun toggleOpenState(state: BlockState, boo: Boolean) {
        level!!.setBlock(this.blockPos, state.setValue(RecordPlayerBlock.OPEN, boo), 3)
        lidController.shouldBeOpen(RecordPlayerBlock.isOpen(state))
    }

    private fun togglePlaying(boo: Boolean) {
        isPlaying = boo
        armController.shouldBeOpen(isPlaying)
    }

    private fun shouldBePlaying(): Boolean {
        // temp logic to be replaced with playback state later
        return getSlottedItem().let{(!it.isEmpty) && isItemValid(it)}
    }

    fun popOutRecord() {
        if (!isEmpty && this.level != null && !this.level!!.isClientSide) {
            // level really likes to be null here
            level?.let{
                val dir = blockState.getValue(FACING).normal
                val vec = Vec3.atLowerCornerWithOffset(blockPos, 0.5, 0.76, 0.5)
                val dirMul = 0.2
                val itemEntity = ItemEntity(it, vec.x, vec.y, vec.z, getSlottedItem().copy())
                itemEntity.setDefaultPickUpDelay()

                /** Give the record some directional velocity forward,
                 * to make it easier to pick up, and so that it doesn't
                 * go backwards and clip through the lid (which looks bad) */
                itemEntity.deltaMovement = Vec3(
                        (2.0 * it.random.nextDouble() - 1) * 0.05f,
                        (2.0 * it.random.nextDouble() - 1) * 0.1f ,
                        (2.0 * it.random.nextDouble() - 1) * 0.05f).add(
                            dir.toVec3().multiply(dirMul, dirMul, dirMul)
                        )

                removeSlottedItem()
                it.addFreshEntity(itemEntity)
            }
        }
    }

    override fun triggerUpdate() {
        val wasPlaying = isPlaying
        togglePlaying(shouldBePlaying())

        // Server side, send a start or stop packet if the state changed.
        if (level?.isClientSide == false && wasPlaying != isPlaying) {
            if (isPlaying) sendPlayRecord() else sendStopRecord()
        }

        super.triggerUpdate()
    }

    private fun sendPlayRecord() {
        val source = PlayRecordPacket.RecordSource.of(getSlottedItem()) ?: return

        ModNetwork.channel.send(
            PacketDistributor.TRACKING_CHUNK.with { level!!.getChunkAt(blockPos) },
            PlayRecordPacket(blockPos, source),
        )
    }

    fun stopPlaying() {
        if (level?.isClientSide == false && isPlaying) {
            togglePlaying(false)
            sendStopRecord()
            dropContents()
        }
    }

    private fun sendStopRecord() {
        ModNetwork.channel.send(
            PacketDistributor.TRACKING_CHUNK.with { level!!.getChunkAt(blockPos) },
            StopRecordPacket(blockPos),
        )
    }

    // todo: once playback is implemented, save and resume song playback state
    override fun load(tag: CompoundTag) {
        super.load(tag)
        tag.takeIf { it.contains(NBT_TAG_PLAYING) }?.let{
            togglePlaying(it.getBoolean(NBT_TAG_PLAYING))
        }

        lidController.shouldBeOpen(RecordPlayerBlock.isOpen(blockState))
    }

    override fun writeTag(tag: CompoundTag): CompoundTag {
        return tag.apply { putBoolean(NBT_TAG_PLAYING, isPlaying) }
    }
}