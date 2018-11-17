package com.codingforcookies.betterrecords.api.sound

import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound

interface IRepeatableSoundHolder {

    fun isRepeatable(stack: ItemStack): Boolean {
        if (stack.hasTagCompound()) {
            val tagCompound = stack.tagCompound!!

            return tagCompound.getBoolean("repeatable")
        }

        return false
    }

    fun setRepeatable(stack: ItemStack, repeatable: Boolean) {
        val tagCompound = if (stack.hasTagCompound()) {
            stack.tagCompound!!
        } else {
            NBTTagCompound()
        }

        tagCompound.setBoolean("repeatable", repeatable)

        stack.tagCompound = tagCompound
    }
}
