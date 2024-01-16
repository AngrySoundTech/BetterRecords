package com.hemogoblins.betterrecords.menu

import com.hemogoblins.betterrecords.BetterRecords
import com.hemogoblins.betterrecords.block.ModBlocks
import com.hemogoblins.betterrecords.block.entity.RecordEtcherBlockEntity
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.ContainerLevelAccess
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.ItemStack
import net.minecraftforge.common.capabilities.ForgeCapabilities
import net.minecraftforge.items.IItemHandler
import net.minecraftforge.items.SlotItemHandler

class RecordEtcherMenu(
    windowId: Int,
    playerInventory: Inventory,
    val blockEntity: RecordEtcherBlockEntity,
): AbstractContainerMenu(ModMenuTypes.RECORD_ETCHER_MENU.get(), windowId) {

    constructor(
        windowId: Int,
        playerInventory: Inventory,
        buf: FriendlyByteBuf
    ) : this(
        windowId,
        playerInventory,
        playerInventory.player.level().getBlockEntity(buf.readBlockPos()) as RecordEtcherBlockEntity
    )

    init {

        // Player Inventory
        for (row in 0..2) {
            for (col in 0..8) {
                addSlot(Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 120 + row * 18))
            }
        }

        // Player Hotbar
        for (i in 0..8) {
            addSlot(Slot(playerInventory, i, 8 + i * 18, 178))
        }

        val itemHandler: IItemHandler = blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER).resolve().get()

        addSlot(object : SlotItemHandler(itemHandler, 0, 17 + 0 * 18, 26 + 0 * 18) {
            override fun mayPlace(stack: ItemStack): Boolean {
                return itemHandler.isItemValid(0, stack)
            }
        })
    }

    override fun quickMoveStack(player: Player, slotIndex: Int): ItemStack {
        val slot: Slot = slots[slotIndex]

        val stack = slot.item
        val stackCopy = stack.copy()

        if (slotIndex < 36) {
            // Inventory has 4*9 (36) slots, so the slot is in the player inventory.
            // If the third argument isn't higher, it doesn't work.
            if (!moveItemStackTo(stack, 36, 37, false)) return ItemStack.EMPTY
        } else if (slotIndex == 36) {
            // Our only slot, so move to the player inventory.
            if (!moveItemStackTo(stack, 0, 35, false)) return ItemStack.EMPTY
        } else {
            // No idea if this would ever happen
            BetterRecords.logger.warn("RecordEtcherMenu for BlockEntity at ${
                blockEntity.blockPos.toShortString()
            } tried quickMoveStack to unexpected index: ${slotIndex})")

            return ItemStack.EMPTY
        }

        if (stack.count == 0) slot.set(ItemStack.EMPTY)
        else slot.setChanged()

        slot.onTake(player, stack)
        return stackCopy
    }

    override fun stillValid(playerIn: Player): Boolean {
        return stillValid(
                ContainerLevelAccess.create(playerIn.level(), blockEntity.blockPos),
                playerIn,
                ModBlocks.RECORD_ETCHER.get()
        )
    }
}
