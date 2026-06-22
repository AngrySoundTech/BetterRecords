package com.hemogoblins.betterrecords.client.sound

import com.hemogoblins.betterrecords.api.client.sound.FileSoundInstance
import net.minecraft.client.Minecraft
import net.minecraft.core.BlockPos

/**
 * Client-side registry of currently playing records
 *
 * A lot of the heavy happens transparently by [com.hemogoblins.betterrecords.mixin.MixinSoundEngine],
 * but this class maps an instance of a playing sound to a position so that we can actually start and stop it.
 */
object RecordSoundManager {

    private val activeSounds = mutableMapOf<BlockPos, FileSoundInstance>()

    /** Starts playing the record at [pos], replacing anything already playing there. */
    fun play(pos: BlockPos, checksum: String) {
        stop(pos)
        val instance = FileSoundInstance(checksum, pos)
        activeSounds[pos] = instance
        Minecraft.getInstance().soundManager.play(instance)
    }

    /** Stops the record playing at [pos], if any. */
    fun stop(pos: BlockPos) {
        activeSounds.remove(pos)?.let {
            Minecraft.getInstance().soundManager.stop(it)
        }
    }
}
