/**
 * The MIT License
 *
 * Copyright (c) 2019 Nicholas Feldman
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package tech.feldman.betterrecords.block.tile

import tech.feldman.betterrecords.block.tile.delegate.CopyOnSetDelegate
import tech.feldman.betterrecords.item.ItemRecord
import net.minecraft.entity.item.EntityItem
import net.minecraft.inventory.IInventory
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.ITickable

class TileRecordEtcher : ModInventoryTile(), IInventory, ITickable {

    var record by CopyOnSetDelegate()

    var recordEntity: EntityItem? = null
        get() {
            if (!record.isEmpty) {
                return EntityItem(world, pos.x.toDouble(), pos.y.toDouble(), pos.z.toDouble(), record)
            }
            return null
        }

    var recordRotation = 0F
    var needleLocation = 0F
    var needleOut = true

    override fun update() {
        if (!record.isEmpty) {
            recordRotation += .08F

            if (needleOut) {
                when {
                    needleLocation < .3F -> needleLocation += .001F
                    else -> needleOut = true
                }
                return
            }
        }

        needleLocation = when {
            needleLocation > 0F -> needleLocation - .005F
            else -> 0F
        }
    }

    override fun getName() = "Record Etcher"

    override fun getSizeInventory() = 1
    override fun getInventoryStackLimit() = 1
    override fun isEmpty() = record.isEmpty

    override fun getStackInSlot(index: Int) = record
    override fun setInventorySlotContents(index: Int, stack: ItemStack) {
        record = stack
    }

    override fun isItemValidForSlot(index: Int, stack: ItemStack): Boolean {
        return stack.item is ItemRecord
    }

    override fun readFromNBT(compound: NBTTagCompound) = compound.run {
        super.readFromNBT(compound)

        record = ItemStack(getCompoundTag("record"))
    }

    override fun writeToNBT(compound: NBTTagCompound) = compound.apply {
        super.writeToNBT(compound)

        setTag("record", getStackTagCompound(record))
    }

    fun getStackTagCompound(stack: ItemStack?): NBTTagCompound {
        val tag = NBTTagCompound()
        stack?.writeToNBT(tag)
        return tag
    }
}
