package com.codingforcookies.betterrecords.item

import com.codingforcookies.betterrecords.api.sound.IColorableSoundHolder
import com.codingforcookies.betterrecords.api.sound.ISoundHolder
import net.minecraft.client.audio.ISound
import net.minecraft.item.ItemStack
import net.minecraft.util.text.translation.I18n as ServerI18n

class ItemFrequencyCrystal(name: String) : ModItem(name), ISoundHolder, IColorableSoundHolder {

    override val maxSounds = 1

    init {
        maxStackSize = 1
    }

    override fun getItemStackDisplayName(stack: ItemStack) =
            if (stack.hasTagCompound() && stack.tagCompound!!.hasKey("local")) {
                stack.tagCompound!!.getString("local")
            } else {
                ServerI18n.translateToLocal("$unlocalizedName.name")
            }
}