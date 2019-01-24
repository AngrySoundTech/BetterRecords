package tech.feldman.betterrecords.helper.nbt

import tech.feldman.betterrecords.api.sound.IRepeatableSoundHolder
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound

private const val REPEATABLE_TAG = "repeatable"

fun isRepeatable(stack: ItemStack): Boolean {
    if (stack.item !is IRepeatableSoundHolder) {
        return false
    }

    if (stack.hasTagCompound()) {
        val tagCompound = stack.tagCompound!!

        return tagCompound.getBoolean(REPEATABLE_TAG)
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

    tagCompound.setBoolean(REPEATABLE_TAG, repeatable)

    stack.tagCompound = tagCompound
}