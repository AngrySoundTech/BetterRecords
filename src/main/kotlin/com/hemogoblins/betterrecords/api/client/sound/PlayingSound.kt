package com.hemogoblins.betterrecords.api.client.sound

/**
 * Display state for currently playing sounds
 */
interface PlayingSound {

    /** Human-readable name  */
    val name: String

    /** True while the audio is still being downloaded or decoded */
    val loading: Boolean

    /** Download or decode progress in the range 0..1. */
    val loadProgress: Float

    /** Timestamp ([net.minecraft.Util.getMillis]) at which playback started. */
    val startedPlayingAt: Long
}
