package com.hemogoblins.betterrecords.capability

import com.hemogoblins.betterrecords.api.capability.IMusicHolder
import net.minecraft.core.Direction
import net.minecraft.nbt.CompoundTag
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.ICapabilitySerializable
import net.minecraftforge.common.util.INBTSerializable
import net.minecraftforge.common.util.LazyOptional

class MusicHolderCapability: IMusicHolder, INBTSerializable<CompoundTag> {

    override var songName = ""
    override var url = ""
    override var checksum = ""

    override fun serializeNBT():CompoundTag {
        val tag = CompoundTag()
        tag.putString("songName", songName)
        tag.putString("url", url)
        tag.putString("checksum", checksum)
        return tag
    }

    override fun deserializeNBT(tag: CompoundTag) {
        songName = tag.getString("songName")
        url = tag.getString("url")
        checksum = tag.getString("checksum")
    }

    class Provider : ICapabilityCompoundable, ICapabilitySerializable<CompoundTag> {
        override val key = "musicHolder"
        private val musicHolder = MusicHolderCapability()


        override fun <T : Any?> getCapability(cap: Capability<T>, side: Direction?): LazyOptional<T> {
            return when (cap) {
                ModCapabilities.MUSIC_HOLDER_CAPABILITY -> LazyOptional.of { musicHolder } as LazyOptional<T>
                else -> LazyOptional.empty()
            }
        }

        override fun serializeNBT(): CompoundTag {
            return musicHolder.serializeNBT()
        }

        override fun deserializeNBT(tag: CompoundTag) {
            return musicHolder.deserializeNBT(tag)
        }
    }

}
