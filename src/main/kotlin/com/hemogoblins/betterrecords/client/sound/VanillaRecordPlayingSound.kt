package com.hemogoblins.betterrecords.client.sound

import com.hemogoblins.betterrecords.api.client.sound.PlayingSound

/**
 * [PlayingSound] for a vanilla music disc.
 */
class VanillaRecordPlayingSound(
    override val name: String,
    override val startedPlayingAt: Long,
) : PlayingSound {
    override val loading = false
    override val loadProgress = 1f
}
