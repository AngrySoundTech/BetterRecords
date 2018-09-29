package com.codingforcookies.betterrecords.helper

import com.codingforcookies.betterrecords.api.sound.Sound
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.nbt.NBTTagList

object RecordHelper {

    fun isRecordFull(stack: ItemStack): Boolean {
        if (stack.hasTagCompound()) {
            val tagCompound = stack.tagCompound!!

            if (tagCompound.hasKey("songs")) {

                val tagList = tagCompound.getTagList("songs", 10)

                if (tagList.tagCount() >= 12) {
                    return true
                }
            }
        }

        return false
    }

    fun addSongToRecord(stack: ItemStack, sound: Sound) {
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
        newSongTag.setString("name", sound.localName)
        newSongTag.setString("url", sound.url)
        newSongTag.setInteger("size", sound.size)

        songList.appendTag(newSongTag)
        tagCompound.setTag("songs", songList)

        stack.tagCompound = tagCompound
    }

    fun getSongsOnRecord(stack: ItemStack): List<Sound> {
        if (stack.hasTagCompound()) {
            val tagCompound = stack.tagCompound!!

            if (tagCompound.hasKey("songs")) {

                val tagList = tagCompound.getTagList("songs", 10)

                return (0 until tagList.tagCount())
                        .map(tagList::getCompoundTagAt)
                        .map {
                            Sound(
                                    localName = it.getString("name"),
                                    url = it.getString("url"),
                                    size = it.getInteger("size")
                            )
                        }
            }
        }

        return emptyList()
    }
}