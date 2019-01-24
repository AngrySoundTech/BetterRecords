package tech.feldman.betterrecords.helper.nbt

import tech.feldman.betterrecords.api.sound.IColorableSoundHolder
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound

private const val COLOR_TAG = "color"

fun getColor(stack: ItemStack): Int {
    if (stack.item !is IColorableSoundHolder) {
        return -1
    }

    if (stack.hasTagCompound()) {
        val tagCompound = stack.tagCompound!!

        return tagCompound.getInteger(COLOR_TAG)
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

    tagCompound.setInteger(COLOR_TAG, color)

    stack.tagCompound = tagCompound
}