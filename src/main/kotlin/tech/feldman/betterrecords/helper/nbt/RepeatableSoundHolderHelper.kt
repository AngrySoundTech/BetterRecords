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