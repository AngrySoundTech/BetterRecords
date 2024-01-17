package com.hemogoblins.betterrecords.block.entity

import com.hemogoblins.betterrecords.capability.ModCapabilities
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.protocol.Packet
import net.minecraft.network.protocol.game.ClientGamePacketListener
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket
import net.minecraft.world.Containers
import net.minecraft.world.SimpleContainer
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.ForgeCapabilities
import net.minecraftforge.common.util.LazyOptional
import net.minecraftforge.items.IItemHandler
import net.minecraftforge.items.ItemStackHandler

/** A block entity with logic for a single-slot inventory, to be extended by:
 *  Etcher, Player, Tuner, Radio */
open class SingleSlotBlockEntity<T : BlockEntity?>(
        pos: BlockPos,
        state: BlockState,
        entityType: BlockEntityType<T>
): BlockEntity(entityType, pos, state) {

    companion object {
        const val NBT_TAG_INVENTORY = "inventory"

        /** might want to change this to check capabilities by generic
         * or just move out of static and override in subs */
        fun isItemValid(itemStack: ItemStack): Boolean {
            return itemStack.getCapability(ModCapabilities.MUSIC_HOLDER_CAPABILITY).isPresent
        }
    }

    protected val itemHandler: ItemStackHandler = object : ItemStackHandler(1) {
        override fun onContentsChanged(slot: Int) {
            setChanged() // Marks dirty and notifies redstone
            triggerUpdate()
        }

        override fun isItemValid(slot: Int, stack: ItemStack): Boolean {
            return isItemValid(stack) && super.isItemValid(slot, stack)
        }
    }

    private val itemHandlerLazy: LazyOptional<IItemHandler> = LazyOptional.of{this.itemHandler}

    val isEmpty: Boolean
        get() = itemHandler.getStackInSlot(0).isEmpty


    fun getSlottedItem(): ItemStack {
        // Is it good to check if it's a MusicHolder here?
        return itemHandler.getStackInSlot(0)/*.takeIf {
            it.getCapability(ModCapabilities.MUSIC_HOLDER_CAPABILITY).isPresent
        } ?: ItemStack.EMPTY*/
    }

    fun removeSlottedItem() {
        this.itemHandler.setStackInSlot(0, ItemStack.EMPTY)
    }


    /** Returns true when item is valid and was inserted */
    fun tryInsertItem(itemStack: ItemStack): Boolean {
        if (isItemValid(itemStack) && this.isEmpty && !itemStack.isEmpty) {
            this.itemHandler.setStackInSlot(0, itemStack.copyWithCount(1))
            itemStack.shrink(1)
            return true
        } else return false
    }

    open fun triggerUpdate() {
        level?.sendBlockUpdated(worldPosition, blockState, blockState, Block.UPDATE_ALL)
    }

    /** Called on destruction */
    fun dropContents() {
        SimpleContainer(itemHandler.slots).let {
            it.setItem(0, itemHandler.getStackInSlot(0))
            this.level?.let { lvl -> Containers.dropContents(lvl, this.worldPosition, it) }
        }
    }

    override fun <T : Any?> getCapability(cap: Capability<T>, side: Direction?): LazyOptional<T> {
        if (cap == ForgeCapabilities.ITEM_HANDLER) return this.itemHandlerLazy.cast()

        return super.getCapability(cap, side)
    }

    override fun setRemoved() {
        super.setRemoved()
        itemHandlerLazy.invalidate()
    }

    override fun invalidateCaps() {
        super.invalidateCaps()
        itemHandlerLazy.invalidate()
    }

    override fun getUpdatePacket(): Packet<ClientGamePacketListener>? {
        return ClientboundBlockEntityDataPacket.create(this)
    }

    override fun load(tag: CompoundTag) {
        super.load(tag)
        tag.takeIf { it.contains(NBT_TAG_INVENTORY) }?.let{
            itemHandler.deserializeNBT(it.getCompound(NBT_TAG_INVENTORY))
        }
    }

    override fun saveAdditional(tag: CompoundTag) {
        tag.also{
            it.put(NBT_TAG_INVENTORY, itemHandler.serializeNBT())
            writeTag(it)
        }
    }

    override fun getUpdateTag(): CompoundTag {
        return CompoundTag().apply {
            put(NBT_TAG_INVENTORY, itemHandler.serializeNBT())
            writeTag(this)
        }
    }

    /** To be overridden by subclasses to write their specific data. */
    open fun writeTag(tag: CompoundTag): CompoundTag {
        // After everything's been implemented, it might be better to write tags more selectively for optimization (per forge docs).
        return tag
    }
}