package com.codingforcookies.betterrecords.api.sound

import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound

interface IShufflableSoundHolder {

    fun isShufflable(stack: ItemStack): Boolean {
        if (stack.hasTagCompound()) {
            val tagCompound = stack.tagCompound!!

            return tagCompound.getBoolean("shufflable")
        }

        return false
    }

    fun setShufflable(stack: ItemStack, shufflable: Boolean) {
        val tagCompound = if (stack.hasTagCompound()) {
            stack.tagCompound!!
        } else {
            NBTTagCompound()
        }

        tagCompound.setBoolean("shufflable", shufflable)

        stack.tagCompound = tagCompound
    }
}
