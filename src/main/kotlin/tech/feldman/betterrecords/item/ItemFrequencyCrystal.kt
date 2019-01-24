package tech.feldman.betterrecords.item

import tech.feldman.betterrecords.api.sound.IColorableSoundHolder
import tech.feldman.betterrecords.api.sound.ISoundHolder
import tech.feldman.betterrecords.helper.nbt.getSounds
import net.minecraft.item.ItemStack
import net.minecraft.util.text.translation.I18n as ServerI18n

class ItemFrequencyCrystal(name: String) : ModItem(name), ISoundHolder, IColorableSoundHolder {

    override val maxSounds = 1

    init {
        maxStackSize = 1
    }

    override fun getUnlocalizedName(stack: ItemStack): String {
        // if there's a song tuned to it, it will be set in [getItemStackDisplayName]
        return "item.betterrecords:frequencycrystal.blank"
    }

    override fun getItemStackDisplayName(stack: ItemStack): String {
        val sounds = getSounds(stack)

        // Use the name of the sound on the crystal if it is tuned
        if (sounds.count() == 1) {
            return sounds.first().name
        }

        // If it has no sounds, we fall back on localizing it.
        return super.getItemStackDisplayName(stack)
    }
}
