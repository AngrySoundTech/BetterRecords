package com.codingforcookies.betterrecords.helper.nbt

import com.codingforcookies.betterrecords.api.sound.IShufflableSoundHolder
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound

fun isShufflable(stack: ItemStack): Boolean {
    if (stack.item !is IShufflableSoundHolder) {
        return false
    }

    if (stack.hasTagCompound()) {
        val tagCompound = stack.tagCompound!!

        return tagCompound.getBoolean("shufflable")
    }

    return false
}

fun setShufflable(stack: ItemStack, shufflable: Boolean) {
    if (stack.item !is IShufflableSoundHolder) {
        return
    }

    val tagCompound = if (stack.hasTagCompound()) {
        stack.tagCompound!!
    } else {
        NBTTagCompound()
    }

    tagCompound.setBoolean("shufflable", shufflable)

    stack.tagCompound = tagCompound
}