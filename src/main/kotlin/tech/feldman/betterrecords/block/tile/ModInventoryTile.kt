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

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.IInventory
import net.minecraft.item.ItemStack

abstract class ModInventoryTile : ModTile(), IInventory {

    override fun getField(id: Int) = 0
    override fun hasCustomName() = true

    override fun removeStackFromSlot(index: Int) = getStackInSlot(index)

    override fun decrStackSize(index: Int, count: Int) = getStackInSlot(index).apply {
        this.let {
            if (this.count <= count) {
                setInventorySlotContents(index, ItemStack.EMPTY)
            } else {
                val splitStack = this.splitStack(count)
                if (splitStack.count == 0) {
                    setInventorySlotContents(index, ItemStack.EMPTY)
                }
                return splitStack
            }
        }
    }

    override fun isUsableByPlayer(player: EntityPlayer) =
            player.getDistanceSq(pos.x + 0.5, pos.y + 0.5, pos.z + 0.5) < 64

    override fun getFieldCount() = 0
    override fun openInventory(player: EntityPlayer?) { /* NO-OP */
    }

    override fun setField(id: Int, value: Int) { /* NO-OP */
    }

    override fun closeInventory(player: EntityPlayer?) { /* NO-OP */
    }

    override fun clear() { /* NO-OP */
    }
}