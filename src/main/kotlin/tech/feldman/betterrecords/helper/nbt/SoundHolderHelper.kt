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

import tech.feldman.betterrecords.api.sound.ISoundHolder
import tech.feldman.betterrecords.api.sound.Sound
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.nbt.NBTTagList

private const val SONGS_TAG = "songs"
private const val NAME_TAG = "name"
private const val URL_TAG = "url"
private const val SIZE_TAG = "size"
private const val AUTHOR_TAG = "author"

fun isFullOfSounds(stack: ItemStack): Boolean {
    if (stack.item !is ISoundHolder) {
        return false
    }

    if (stack.hasTagCompound()) {
        val tagCompound = stack.tagCompound!!

        if (tagCompound.hasKey(SONGS_TAG)) {

            val tagList = tagCompound.getTagList(SONGS_TAG, 10)

            if (tagList.tagCount() >= (stack.item as ISoundHolder).maxSounds) {
                return true
            }
        }
    }

    return false
}

fun addSound(stack: ItemStack, sound: Sound) {
    if (stack.item !is ISoundHolder) {
        return
    }

    val tagCompound = if (stack.hasTagCompound()) {
        stack.tagCompound!!
    } else {
        NBTTagCompound()
    }

    val songList = if (tagCompound.hasKey(SONGS_TAG)) {
        tagCompound.getTagList(SONGS_TAG, 10)
    } else {
        NBTTagList()
    }

    val newSongTag = NBTTagCompound()
    newSongTag.setString(NAME_TAG, sound.name)
    newSongTag.setString(URL_TAG, sound.url)
    newSongTag.setInteger(SIZE_TAG, sound.size)
    newSongTag.setString(AUTHOR_TAG, sound.author)

    songList.appendTag(newSongTag)
    tagCompound.setTag(SONGS_TAG, songList)

    stack.tagCompound = tagCompound
}

fun getSounds(stack: ItemStack): List<Sound> {
    if (stack.item !is ISoundHolder) {
        return emptyList()
    }

    if (stack.hasTagCompound()) {
        val tagCompound = stack.tagCompound!!

        if (tagCompound.hasKey(SONGS_TAG)) {

            val tagList = tagCompound.getTagList(SONGS_TAG, 10)

            return (0 until tagList.tagCount())
                    .map(tagList::getCompoundTagAt)
                    .map {
                        Sound(
                                name = it.getString(NAME_TAG),
                                url = it.getString(URL_TAG),
                                size = it.getInteger(SIZE_TAG),
                                author = it.getString(AUTHOR_TAG)
                        )
                    }
        }
    }

    return emptyList()
}
