package com.hemogoblins.betterrecords.client.sound.format

import com.hemogoblins.betterrecords.api.client.sound.AudioStreamProvider
import com.hemogoblins.betterrecords.api.client.sound.RecordAudioStream
import com.mojang.blaze3d.audio.OggAudioStream
import java.io.InputStream
import java.nio.ByteBuffer
import javax.sound.sampled.AudioFormat

/**
 * Ogg Vorbis support, backed by Minecraft's [OggAudioStream] (stb_vorbis).
 */
object OggAudioStreamProvider : AudioStreamProvider {

    override val id = "ogg"

    // Every Ogg bitstream page begins with the capture pattern "OggS".
    override fun matches(header: ByteArray): Boolean =
        header.size >= 4 &&
            header[0] == 'O'.code.toByte() &&
            header[1] == 'g'.code.toByte() &&
            header[2] == 'g'.code.toByte() &&
            header[3] == 'S'.code.toByte()

    override fun create(input: InputStream): RecordAudioStream =
        OggRecordAudioStream(OggAudioStream(input))

    private class OggRecordAudioStream(private val delegate: OggAudioStream) : RecordAudioStream {
        override val format: AudioFormat get() = delegate.format
        override fun readAll(): ByteBuffer = delegate.readAll()
        override fun close() = delegate.close()
    }
}
