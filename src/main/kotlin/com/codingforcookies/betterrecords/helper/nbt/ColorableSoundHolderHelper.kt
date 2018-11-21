package com.codingforcookies.betterrecords.helper.nbt

import com.codingforcookies.betterrecords.api.sound.IColorableSoundHolder
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound

fun getColor(stack: ItemStack): Int {
    if (stack.item !is IColorableSoundHolder) {
        return -1
    }

    if (stack.hasTagCompound()) {
        val tagCompound = stack.tagCompound!!

        return tagCompound.getInteger("color")
    }

    return -1
}

fun setColor(stack: ItemStack, color: Int) {
    if (stack.item !is IColorableSoundHolder) {
        return
    }

    val tagCompound = if (stack.hasTagCompound()) {
        stack.tagCompound!!
    } else {
        NBTTagCompound()
    }

    tagCompound.setInteger("color", color)

    stack.tagCompound = tagCompound
}