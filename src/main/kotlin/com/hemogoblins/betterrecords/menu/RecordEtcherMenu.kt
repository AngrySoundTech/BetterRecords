package com.hemogoblins.betterrecords.menu

import com.hemogoblins.betterrecords.capability.ModCapabilities
import net.minecraft.world.Container
import net.minecraft.world.SimpleContainer
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.ItemStack

class RecordEtcherMenu(
    windowId: Int,
    playerInventory: Inventory,
    val container: Container = SimpleContainer(1),
): AbstractContainerMenu(ModMenuTypes.RECORD_ETCHER_MENU.get(), windowId) {
    init {
        addSlot(object : Slot(container, 0, 17 + 0 * 18, 26 + 0 * 18) {
            override fun mayPlace(stack: ItemStack): Boolean {
                return stack.getCapability(ModCapabilities.MUSIC_HOLDER_CAPABILITY).isPresent
            }
        })

        // Player Inventory
        for (row in 0..2) {
            for (col in 0..8) {
                addSlot(Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 120 + row * 18))
            }
        }
        for (i in 0..8) {
            addSlot(Slot(playerInventory, i, 8 + i * 18, 178))
        }
    }

    override fun quickMoveStack(p_38941_: Player, p_38942_: Int): ItemStack {
        // TODO
        return ItemStack.EMPTY
    }

    override fun stillValid(p_38874_: Player): Boolean {
        // TODO
        return true;
    }
}
