package com.codingforcookies.betterrecords.item

import com.codingforcookies.betterrecords.api.sound.ISoundHolder
import net.minecraft.client.resources.I18n
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.item.ItemStack
import net.minecraft.world.World

class ItemNewRecord(name: String) : ModItem(name), ISoundHolder {

    init {
        maxStackSize = 1
    }

    override fun getUnlocalizedName(stack: ItemStack): String {
        if (stack.hasTagCompound()) {
            val tagCompound = stack.tagCompound!!

            if (tagCompound.hasKey("songs")) {
                val tagList = tagCompound.getTagList("songs", 10)

                return if (tagList.tagCount() == 1) {
                    "item.betterrecords:record.single"
                } else {
                    "item.betterrecords:record.multi"
                }
            }
        }

        return "item.betterrecords:record.blank"
    }

    override fun addInformation(stack: ItemStack, world: World?, tooltip: MutableList<String>, flag: ITooltipFlag) {
        if (!stack.hasTagCompound()) {
            return
        }

        val tagCompound = stack.tagCompound!!

        // We only want to add information to the record if it has songs, otherwise it's blank.
        if (tagCompound.hasKey("songs")) {
            val tagList = tagCompound.getTagList("songs", 10)

            if (tagList.tagCount() > 1) {
                // If we have multiple songs on the record,
                // we want to list them out and display the total size.

                // Add all the songs in an itemized list
                (0 until tagList.tagCount())
                        .map(tagList::getCompoundTagAt)
                        .forEachIndexed { index, songTag ->
                            tooltip += I18n.format(
                                    "item.betterrecords:record.desc.songentry", index + 1, songTag.getString("name"))
                        }
            } else {
                // If we only have one song on the record,
                // we don't want to display an itemized list.

                val songTag = tagList.getCompoundTagAt(0)
                // Add our one song to the tooltip, with a different formatting
                tooltip += I18n.format("item.betterrecords:record.desc.song", songTag.getString("name"))
            }

            // Calculate total size of the record from tags
            val size = (0 until tagList.tagCount())
                    .map(tagList::getCompoundTagAt)
                    .sumBy { it.getInteger("size") }

            // Add our total size to the record
            tooltip += I18n.format("item.betterrecords:record.desc.size", size)
        }
    }
}
