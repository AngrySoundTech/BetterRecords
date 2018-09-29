package com.codingforcookies.betterrecords.item

import com.codingforcookies.betterrecords.api.sound.IRepeatableSoundHolder
import com.codingforcookies.betterrecords.api.sound.IShufflableSoundHolder
import com.codingforcookies.betterrecords.api.sound.ISoundHolder
import net.minecraft.client.resources.I18n
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.item.ItemStack
import net.minecraft.world.World

class ItemNewRecord(name: String) : ModItem(name), ISoundHolder, IRepeatableSoundHolder, IShufflableSoundHolder {

    init {
        maxStackSize = 1
    }

    override fun getUnlocalizedName(stack: ItemStack): String {
        val songCount = getSounds(stack).count()

        return when (songCount) {
            0    -> "item.betterrecords:record.blank"
            1    -> "item.betterrecords:record.single"
            else -> "item.betterrecords:record.multi"
        }
    }

    override fun addInformation(stack: ItemStack, world: World?, tooltip: MutableList<String>, flag: ITooltipFlag) {
        val songs = getSounds(stack)

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

            // If it's repeatable, we show that.
            if (isRepeatable(stack)) {
                tooltip += "\u00a7e" + I18n.format("item.betterrecords:record.desc.repeatable")
            }

            // If it's shufflable, we show that.
            if (isShufflable(stack)) {
                tooltip += "\u00a7e" + I18n.format("item.betterrecords:record.desc.shufflable")
            }
        }
    }
}
