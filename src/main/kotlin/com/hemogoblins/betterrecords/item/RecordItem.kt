package com.hemogoblins.betterrecords.item

import com.hemogoblins.betterrecords.capability.ColorableCapability
import com.hemogoblins.betterrecords.capability.CompoundCapabilityProvider
import com.hemogoblins.betterrecords.capability.ModCapabilities
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.level.Level
import net.minecraftforge.common.capabilities.ICapabilityProvider

class RecordItem(properties: Properties) : Item(properties) {

    override fun appendHoverText(stack: ItemStack, world: Level?, tooltip: MutableList<Component>, flag: TooltipFlag) {
        val cap = stack.getCapability(ModCapabilities.COLORABLE_CAPABILITY)

        cap.ifPresent {
            tooltip.add(Component.literal(it.color.toString()))
        }

        super.appendHoverText(stack, world, tooltip, flag)
    }

    override fun initCapabilities(stack: ItemStack, nbt: CompoundTag?): ICapabilityProvider {
        return CompoundCapabilityProvider(
            ColorableCapability.Provider(),
        )
    }
}
