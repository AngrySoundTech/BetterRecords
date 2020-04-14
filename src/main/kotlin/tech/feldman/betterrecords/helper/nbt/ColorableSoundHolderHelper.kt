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