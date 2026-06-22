package com.hemogoblins.betterrecords.client.sound

import com.hemogoblins.betterrecords.BetterRecords
import com.hemogoblins.betterrecords.api.client.sound.FileSoundInstance
import net.minecraft.Util
import net.minecraft.client.Minecraft
import net.minecraft.core.BlockPos
import java.util.concurrent.CompletableFuture

/**
 * Client-side registry of currently playing records
 *
 * A lot of the heavy happens transparently by [com.hemogoblins.betterrecords.mixin.MixinSoundEngine],
 * but this class maps an instance of a playing sound to a position so that we can actually start and stop it.
 */
object RecordSoundManager {

    private val activeSounds = mutableMapOf<BlockPos, FileSoundInstance>()

    /** Starts playing the record at [pos], replacing anything already playing there. */
    fun play(pos: BlockPos, checksum: String, url: String, name: String) {
        stop(pos)

        val instance = FileSoundInstance(checksum, pos, name).apply {
            loading = true
            loadProgress = 0f
        }
        activeSounds[pos] = instance

        CompletableFuture
            .runAsync({
                BetterRecords.cache.get(url, checksum) { percent ->
                    instance.loadProgress = (percent / 100f).coerceIn(0f, 1f)
                }
            }, Util.ioPool())
            .thenRunAsync({
                // The record may have been removed or replaced while downloading.
                if (activeSounds[pos] === instance) {
                    Minecraft.getInstance().soundManager.play(instance)
                }
            }, Minecraft.getInstance())
            .exceptionallyAsync({ throwable ->
                if (activeSounds[pos] === instance) {
                    activeSounds.remove(pos)
                    instance.loading = false
                }
                BetterRecords.logger.error("Failed to load record at $pos", throwable)
                null
            }, Minecraft.getInstance())
    }

    /** Stops the record playing at [pos], if any. */
    fun stop(pos: BlockPos) {
        activeSounds.remove(pos)?.let {
            Minecraft.getInstance().soundManager.stop(it)
        }
    }

    fun onFinished(instance: FileSoundInstance) {
        if (activeSounds[instance.pos] === instance) {
            activeSounds.remove(instance.pos)
        }
    }

    fun activeRecords(): List<FileSoundInstance> = activeSounds.values.toList()
}
