package com.codingforcookies.betterrecords.api.sound

import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.nbt.NBTTagList

interface ISoundHolder {

    val maxSounds: Int

    fun isFullOfSounds(stack: ItemStack): Boolean {
        if (stack.hasTagCompound()) {
            val tagCompound = stack.tagCompound!!

            if (tagCompound.hasKey("songs")) {

                val tagList = tagCompound.getTagList("songs", 10)

                if (tagList.tagCount() >= maxSounds) {
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

        val songList = if (tagCompound.hasKey("songs")) {
            tagCompound.getTagList("songs", 10)
        } else {
            NBTTagList()
        }

        val newSongTag = NBTTagCompound()
        newSongTag.setString("name", sound.name)
        newSongTag.setString("url", sound.url)
        newSongTag.setInteger("size", sound.size)
        newSongTag.setString("author", sound.author)

        songList.appendTag(newSongTag)
        tagCompound.setTag("songs", songList)

        stack.tagCompound = tagCompound
    }

    fun getSounds(stack: ItemStack): List<Sound> {
        if (stack.item !is ISoundHolder) {
            return emptyList()
        }

        if (stack.hasTagCompound()) {
            val tagCompound = stack.tagCompound!!

            if (tagCompound.hasKey("songs")) {

                val tagList = tagCompound.getTagList("songs", 10)

                return (0 until tagList.tagCount())
                        .map(tagList::getCompoundTagAt)
                        .map {
                            Sound(
                                    name = it.getString("name"),
                                    url = it.getString("url"),
                                    size = it.getInteger("size"),
                                    author = it.getString("author")
                            )
                        }
            }
        }

        return emptyList()
    }
}
