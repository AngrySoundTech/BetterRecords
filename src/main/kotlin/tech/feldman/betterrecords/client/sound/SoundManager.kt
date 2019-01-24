package tech.feldman.betterrecords.client.sound

import tech.feldman.betterrecords.api.sound.Sound
import net.minecraft.util.math.BlockPos
import kotlin.concurrent.thread

object SoundManager {

    private val jobs = hashMapOf<Pair<BlockPos, Int>, Thread>()

    fun queueSongsAt(pos: BlockPos, dimension: Int, sounds: List<Sound>, shuffle: Boolean = false, repeat: Boolean = false) {
        val job = thread {
            while (true) {
                sounds.forEach {
                    SoundPlayer.playSound(pos, dimension, it)
                }

                if (!repeat) {
                    return@thread
                }
            }
        }

        jobs[Pair(pos, dimension)] = job
    }

    fun queueStreamAt(pos: BlockPos, dimension: Int, sound: Sound) {
        val job = thread {
            SoundPlayer.playSoundFromStream(pos, dimension, sound)
        }

        jobs[Pair(pos, dimension)] = job
    }

    fun stopQueueAt(pos: BlockPos, dimension: Int) {
        SoundPlayer.stopPlayingAt(pos, dimension)
        jobs[Pair(pos, dimension)]?.stop()
        jobs.remove(Pair(pos, dimension))
    }
}
