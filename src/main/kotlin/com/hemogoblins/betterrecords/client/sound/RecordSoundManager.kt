package com.hemogoblins.betterrecords.client.sound

import com.hemogoblins.betterrecords.BetterRecords
import com.hemogoblins.betterrecords.api.client.sound.PlayingSound
import com.hemogoblins.betterrecords.network.clientbound.PlayRecordPacket
import net.minecraft.Util
import net.minecraft.client.Minecraft
import net.minecraft.client.resources.sounds.SimpleSoundInstance
import net.minecraft.client.resources.sounds.SoundInstance
import net.minecraft.core.BlockPos
import net.minecraft.world.phys.Vec3
import net.minecraftforge.registries.ForgeRegistries
import java.util.concurrent.CompletableFuture
import net.minecraft.world.item.RecordItem as VanillaRecordItem

/**
 * Client-side registry of currently playing records
 *
 * A lot of the heavy lifting happens transparently in [com.hemogoblins.betterrecords.mixin.MixinSoundEngine]
 * for file-backed sounds, but this class maps a playing sound to a position so that we can actually
 * start and stop it.
 */
object RecordSoundManager {

    /** A sound playing at a position, paired with the [PlayingSound] the overlay displays for it. */
    private class ActiveSound(val sound: SoundInstance, val display: PlayingSound)

    private val activeSounds = mutableMapOf<BlockPos, ActiveSound>()

    /** Starts playing [source] at [pos], replacing anything already playing there. */
    fun play(pos: BlockPos, source: PlayRecordPacket.RecordSource) {
        stop(pos)

        when (source) {
            is PlayRecordPacket.RecordSource.File -> playFile(pos, source)
            is PlayRecordPacket.RecordSource.Vanilla -> playVanilla(pos, source)
        }
    }

    /** Plays a BetterRecords record: downloads/decodes the cached file, then plays it via the mixin. */
    private fun playFile(pos: BlockPos, source: PlayRecordPacket.RecordSource.File) {
        val instance = RecordPlayingSound(source.checksum, pos, source.name).apply {
            loading = true
            loadProgress = 0f
        }
        activeSounds[pos] = ActiveSound(instance, instance)

        CompletableFuture
            .runAsync({
                BetterRecords.cache.get(source.url, source.checksum) { percent ->
                    instance.loadProgress = (percent / 100f).coerceIn(0f, 1f)
                }
            }, Util.ioPool())
            .thenRunAsync({
                // The record may have been removed or replaced while downloading.
                if (activeSounds[pos]?.sound === instance) {
                    Minecraft.getInstance().soundManager.play(instance)
                }
            }, Minecraft.getInstance())
            .exceptionallyAsync({ throwable ->
                if (activeSounds[pos]?.sound === instance) {
                    activeSounds.remove(pos)
                    instance.loading = false
                }
                BetterRecords.logger.error("Failed to load record at $pos", throwable)
                null
            }, Minecraft.getInstance())
    }

    /** Plays a vanilla music disc through vanilla's sound engine. */
    private fun playVanilla(pos: BlockPos, source: PlayRecordPacket.RecordSource.Vanilla) {
        val event = ForgeRegistries.SOUND_EVENTS.getValue(source.sound)
        if (event == null) {
            BetterRecords.logger.warn("Unknown sound event for vanilla disc at $pos: ${source.sound}")
            return
        }

        val instance = SimpleSoundInstance.forRecord(event, Vec3.atCenterOf(pos))
        val name = VanillaRecordItem.getBySound(event)?.displayName?.string ?: "Record"
        activeSounds[pos] = ActiveSound(instance, VanillaRecordPlayingSound(name, Util.getMillis()))
        Minecraft.getInstance().soundManager.play(instance)
    }

    /** Stops the record playing at [pos], if any. */
    fun stop(pos: BlockPos) {
        activeSounds.remove(pos)?.let {
            Minecraft.getInstance().soundManager.stop(it.sound)
        }
    }

    fun onFinished(instance: RecordPlayingSound) {
        if (activeSounds[instance.pos]?.sound === instance) {
            activeSounds.remove(instance.pos)
        }
    }

    /** Sounds currently playing, used by the loading / "Now Playing" overlay. */
    fun activeRecords(): List<PlayingSound> = activeSounds.values.map { it.display }
}
