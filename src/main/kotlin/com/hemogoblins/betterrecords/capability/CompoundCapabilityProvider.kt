package com.hemogoblins.betterrecords.capability

import net.minecraft.core.Direction
import net.minecraft.nbt.CompoundTag
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.ICapabilitySerializable
import net.minecraftforge.common.util.LazyOptional

// TODO: Replace with something that supports all providers
interface ICapabilityCompoundable : ICapabilitySerializable<CompoundTag> {
    val key: String
}

class CompoundCapabilityProvider(
    private vararg val providers: ICapabilityCompoundable
): ICapabilitySerializable<CompoundTag> {

    override fun <T : Any?> getCapability(cap: Capability<T>, side: Direction?): LazyOptional<T> {
        return providers
            .map { it.getCapability(cap, side) }
            .first { it.isPresent }
    }

    override fun serializeNBT(): CompoundTag {
        return CompoundTag().apply {
            providers.forEach {
                put(it.key, it.serializeNBT())
            }
        }
    }

    override fun deserializeNBT(nbt: CompoundTag) {
        nbt.allKeys.forEach { nbtKey ->
            providers
                .firstOrNull { it.key == nbtKey }
                ?.deserializeNBT(nbt.getCompound(nbtKey))
        }
    }
}