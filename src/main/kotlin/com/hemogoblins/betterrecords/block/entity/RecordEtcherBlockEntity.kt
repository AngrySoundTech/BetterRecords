package com.hemogoblins.betterrecords.block.entity

import com.hemogoblins.betterrecords.BetterRecords
import com.hemogoblins.betterrecords.block.ModBlocks
import com.hemogoblins.betterrecords.capability.ModCapabilities
import com.hemogoblins.betterrecords.menu.RecordEtcherMenu
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.network.protocol.Packet
import net.minecraft.network.protocol.game.ClientGamePacketListener
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket
import net.minecraft.world.Containers
import net.minecraft.world.MenuProvider
import net.minecraft.world.SimpleContainer
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.ForgeCapabilities
import net.minecraftforge.common.util.LazyOptional
import net.minecraftforge.items.IItemHandler
import net.minecraftforge.items.ItemStackHandler

class RecordEtcherBlockEntity(
    pos: BlockPos,
    state: BlockState
): BlockEntity(ModBlocks.RECORD_ETCHER_ENTITY.get(), pos, state), MenuProvider {

    companion object {
        val NBT_TAG_INVENTORY = "inventory"
    }

    private val itemHandler: ItemStackHandler = object : ItemStackHandler(1) {
        override fun onContentsChanged(slot: Int) {
            setChanged() // Marks dirty and notifies redstone
            triggerUpdate()
        }
    }

    private val itemHandlerLazy: LazyOptional<IItemHandler> = LazyOptional.of{this.itemHandler}

    override fun createMenu(windowId: Int, inventory: Inventory, player: Player): AbstractContainerMenu {
        return RecordEtcherMenu(windowId, inventory, this)
    }

    override fun getDisplayName(): Component {
        return Component.translatable("menu.${BetterRecords.ID}.record_etcher.title")
    }

    fun getSlottedRecord(): ItemStack {
        return itemHandler.getStackInSlot(0).takeIf {
            it.getCapability(ModCapabilities.MUSIC_HOLDER_CAPABILITY).isPresent
        } ?: ItemStack.EMPTY
    }

    fun triggerUpdate() {
        level?.sendBlockUpdated(worldPosition, blockState, blockState, Block.UPDATE_ALL)
    }

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

    override fun load(tag: CompoundTag) {
        super.load(tag)
        tag.takeIf { it.contains(NBT_TAG_INVENTORY) }?.let{
            itemHandler.deserializeNBT(it.getCompound(NBT_TAG_INVENTORY))
        }
    }

    override fun saveAdditional(tag: CompoundTag) {
        tag.put(NBT_TAG_INVENTORY, itemHandler.serializeNBT())
    }

    override fun getUpdateTag(): CompoundTag {
        return CompoundTag().apply {
            put(NBT_TAG_INVENTORY, itemHandler.serializeNBT())
        }
    }

    override fun getUpdatePacket(): Packet<ClientGamePacketListener>? {
        return ClientboundBlockEntityDataPacket.create(this)
    }
}
