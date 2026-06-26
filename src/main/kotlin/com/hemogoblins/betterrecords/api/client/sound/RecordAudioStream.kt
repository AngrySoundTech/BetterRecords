package com.hemogoblins.betterrecords.api.client.sound

import java.io.Closeable
import java.io.IOException
import java.nio.ByteBuffer
import javax.sound.sampled.AudioFormat

/**
 * A source of decoded, signed 16-bit PCM audio ready for OpenAL.
 */
interface RecordAudioStream : Closeable {

    /**
     * The format of the PCM returned by [readAll]. Channels must be mono or stereo
     * and the sample size 8 or 16 bit, those are the only layouts OpenAL accepts.
     */
    val format: AudioFormat

    /**
     * Fully decodes the stream into a single direct [ByteBuffer] of PCM
     */
    @Throws(IOException::class)
    fun readAll(): ByteBuffer
}
