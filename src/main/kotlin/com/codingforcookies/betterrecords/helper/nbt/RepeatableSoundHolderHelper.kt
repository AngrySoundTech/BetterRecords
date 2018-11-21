package com.codingforcookies.betterrecords.helper.nbt

import com.codingforcookies.betterrecords.api.sound.IRepeatableSoundHolder
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound

fun isRepeatable(stack: ItemStack): Boolean {
    if (stack.item !is IRepeatableSoundHolder) {
        return false
    }

    if (stack.hasTagCompound()) {
        val tagCompound = stack.tagCompound!!

        return tagCompound.getBoolean("repeatable")
    }

    return false
}

fun setRepeatable(stack: ItemStack, repeatable: Boolean) {
    if (stack.item !is IRepeatableSoundHolder) {
        return
    }

    val tagCompound = if (stack.hasTagCompound()) {
        stack.tagCompound!!
    } else {
        NBTTagCompound()
    }

    tagCompound.setBoolean("repeatable", repeatable)

    stack.tagCompound = tagCompound
}