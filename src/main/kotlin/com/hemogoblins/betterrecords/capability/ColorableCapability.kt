package com.hemogoblins.betterrecords.capability

import com.hemogoblins.betterrecords.api.capability.IColorable
import net.minecraft.core.Direction
import net.minecraft.nbt.CompoundTag
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.ICapabilitySerializable
import net.minecraftforge.common.util.INBTSerializable
import net.minecraftforge.common.util.LazyOptional

class ColorableCapability: IColorable, INBTSerializable<CompoundTag> {

    override var color = 0xFFFFFF

    override fun serializeNBT(): CompoundTag {
        return CompoundTag().apply {
            putInt("color", color)
        }
    }

    override fun deserializeNBT(nbt: CompoundTag) {
        color = nbt.getInt("color")
    }

    class Provider : ICapabilitySerializable<CompoundTag> {
        private val colorable = ColorableCapability()

        override fun <T : Any?> getCapability(cap: Capability<T>, side: Direction?): LazyOptional<T> {
            return when (cap) {
                ModCapabilities.COLORABLE_CAPABILITY -> LazyOptional.of { colorable } as LazyOptional<T>
                else -> LazyOptional.empty()
            }
        }

        override fun serializeNBT(): CompoundTag {
            return colorable.serializeNBT()
        }

        override fun deserializeNBT(tag: CompoundTag) {
            colorable.deserializeNBT(tag)
        }
    }
}
