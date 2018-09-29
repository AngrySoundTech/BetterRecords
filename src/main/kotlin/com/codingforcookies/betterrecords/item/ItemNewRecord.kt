package com.codingforcookies.betterrecords.item

import com.codingforcookies.betterrecords.api.sound.ISoundHolder
import com.codingforcookies.betterrecords.helper.RecordHelper
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
        val songs = RecordHelper.getSongsOnRecord(stack)

        // We only want to add a tooltip to the record if it is not empty
        if (songs.isNotEmpty()) {

            // Add the song info to the tooltip
            if (songs.size > 1) {
                // If there are multiple songs on the record, we want to display them all in an itemized list
                songs.forEachIndexed { index, sound ->
                    tooltip += I18n.format("item.betterrecords:record.desc.songentry", index + 1, sound.localName)
                }
            } else {
                // If there is only one song on the record, we want to display just it.
                tooltip += I18n.format("item.betterrecords:record.desc.song", songs.first().localName)
            }

            // Add the size to the record.
            val size = songs.sumBy { it.size }
            tooltip += I18n.format("item.betterrecords:record.desc.size", size)
        }
    }
}
