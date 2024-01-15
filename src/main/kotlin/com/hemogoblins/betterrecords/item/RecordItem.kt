package com.hemogoblins.betterrecords.item

import com.hemogoblins.betterrecords.capability.ColorableCapability
import com.hemogoblins.betterrecords.capability.CompoundCapabilityProvider
import com.hemogoblins.betterrecords.capability.ModCapabilities
import com.hemogoblins.betterrecords.capability.MusicHolderCapability
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.level.Level
import net.minecraftforge.common.capabilities.ICapabilityProvider

class RecordItem(properties: Properties) : Item(properties) {

    override fun appendHoverText(stack: ItemStack, world: Level?, tooltip: MutableList<Component>, flag: TooltipFlag) {
        val colorCapability = stack.getCapability(ModCapabilities.COLORABLE_CAPABILITY)
        val musicHolderCapability = stack.getCapability(ModCapabilities.MUSIC_HOLDER_CAPABILITY)

        colorCapability.ifPresent {
            tooltip.add(Component.literal(it.color.toString()))
        }

        musicHolderCapability.ifPresent {
            tooltip.add(Component.literal(it.songName))
        }

        super.appendHoverText(stack, world, tooltip, flag)
    }

    override fun initCapabilities(stack: ItemStack, nbt: CompoundTag?): ICapabilityProvider {
        return CompoundCapabilityProvider(
            ColorableCapability.Provider(), MusicHolderCapability.Provider()
        )
    }
}
