package com.hemogoblins.betterrecords.api.client.sound

import com.hemogoblins.betterrecords.BetterRecords
import net.minecraft.client.resources.sounds.Sound
import net.minecraft.client.resources.sounds.SoundInstance
import net.minecraft.client.sounds.SoundManager
import net.minecraft.client.sounds.WeighedSoundEvents
import net.minecraft.core.BlockPos
import net.minecraft.resources.ResourceLocation
import net.minecraft.sounds.SoundSource
import net.minecraft.util.valueproviders.ConstantFloat

class FileSoundInstance(
    val file: String,
    val pos: BlockPos,
    /** Human-readable name */
    val name: String = "",
) : SoundInstance {

    /** True while still being decoded by  [com.hemogoblins.betterrecords.mixin.MixinSoundEngine]. */
    @Volatile
    var loading: Boolean = false

    /** Decode progress in the range 0..1. */
    @Volatile
    var loadProgress: Float = 0f

    /**
     * Timestamp at which playback actually started
     */
    @Volatile
    var startedPlayingAt: Long = 0L

    override fun getLocation() = ResourceLocation(BetterRecords.ID, file)

    override fun resolve(soundManager: SoundManager): WeighedSoundEvents?  = null

    // getSound/getVolume/getPitch exist only to satisfy the SoundInstance contract.
    // Playback is handled by MixinSoundEngine
    override fun getSound() = Sound(
        location.toString(),
        ConstantFloat.of(volume),
        ConstantFloat.of(pitch),
        1,
        Sound.Type.SOUND_EVENT,
        true,
        false,
        64
    )

    override fun getSource() = SoundSource.RECORDS

    override fun isLooping() = false

    override fun isRelative() = false

    override fun getDelay() = 0

    override fun getVolume() = 1.0F

    override fun getPitch() = 1.0F

    override fun getX() = pos.x.toDouble()

    override fun getY() = pos.y.toDouble()

    override fun getZ() = pos.z.toDouble()

    override fun getAttenuation() = SoundInstance.Attenuation.LINEAR
}
