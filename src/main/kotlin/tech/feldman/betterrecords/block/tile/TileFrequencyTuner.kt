package tech.feldman.betterrecords.block.tile

import tech.feldman.betterrecords.api.sound.ISoundHolder
import tech.feldman.betterrecords.block.tile.delegate.CopyOnSetDelegate
import tech.feldman.betterrecords.helper.nbt.getSounds
import tech.feldman.betterrecords.item.ItemFrequencyCrystal
import net.minecraft.inventory.IInventory
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.ITickable

class TileFrequencyTuner : ModInventoryTile(), IInventory, ITickable {

    var crystal by CopyOnSetDelegate()

    var crystalFloaty = 0F

    override fun getName() = "Frequency Tuner"

    override fun getSizeInventory() = 1
    override fun getInventoryStackLimit() = 1
    override fun isEmpty() = crystal.isEmpty

    override fun getStackInSlot(index: Int) = crystal

    override fun isItemValidForSlot(index: Int, stack: ItemStack) =
            stack.item is ItemFrequencyCrystal && getSounds(stack).isEmpty()

    override fun setInventorySlotContents(index: Int, stack: ItemStack) {
        crystal = stack
    }

    override fun update() {
        crystal.let {
            crystalFloaty += 0.86F
        }
    }

    override fun readFromNBT(compound: NBTTagCompound) = compound.run {
        super.readFromNBT(compound)

        crystal = ItemStack(getCompoundTag("crystal"))
        crystal = ItemStack(getCompoundTag("crystal"))
    }

    override fun writeToNBT(compound: NBTTagCompound) = compound.apply {
        super.writeToNBT(compound)

        setTag("crystal", getStackTagCompound(crystal))
    }

    fun getStackTagCompound(stack: ItemStack?): NBTTagCompound {
        val tag = NBTTagCompound()
        stack?.writeToNBT(tag)
        return tag
    }

}
