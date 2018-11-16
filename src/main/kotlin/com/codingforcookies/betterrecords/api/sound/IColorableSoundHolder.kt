package com.codingforcookies.betterrecords.api.sound

import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound

interface IColorableSoundHolder {

    fun getColor(stack: ItemStack): Int {
        if (stack.hasTagCompound()) {
            val tagCompound = stack.tagCompound!!

            return tagCompound.getInteger("color")
        }

        return -1
    }

    fun setColor(stack: ItemStack, color: Int) {
        val tagCompound = if (stack.hasTagCompound()) {
            stack.tagCompound!!
        } else {
            NBTTagCompound()
        }

        tagCompound.setInteger("color", color)

        stack.tagCompound = tagCompound
    }
}
