package tech.feldman.betterrecords.helper.nbt

import tech.feldman.betterrecords.api.sound.IShufflableSoundHolder
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound

private const val SHUFFLABLE_TAG = "shufflable"

fun isShufflable(stack: ItemStack): Boolean {
    if (stack.item !is IShufflableSoundHolder) {
        return false
    }

    if (stack.hasTagCompound()) {
        val tagCompound = stack.tagCompound!!

        return tagCompound.getBoolean(SHUFFLABLE_TAG)
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

    tagCompound.setBoolean(SHUFFLABLE_TAG, shufflable)

    stack.tagCompound = tagCompound
}