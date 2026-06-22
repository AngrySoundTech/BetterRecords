package com.hemogoblins.betterrecords.client.sound.format

import com.hemogoblins.betterrecords.api.client.sound.AudioStreamProvider
import com.hemogoblins.betterrecords.api.client.sound.RecordAudioStream
import javazoom.jl.decoder.Bitstream
import javazoom.jl.decoder.BitstreamException
import javazoom.jl.decoder.Decoder
import javazoom.jl.decoder.DecoderException
import javazoom.jl.decoder.Header
import javazoom.jl.decoder.SampleBuffer
import org.lwjgl.BufferUtils
import java.io.IOException
import java.io.InputStream
import java.nio.ByteBuffer
import javax.sound.sampled.AudioFormat

/**
 * MP3 support, backed by the JLayer decoder.
 *
 * JLayer decodes MP3 frames into interleaved signed 16-bit PCM, which is exactly what OpenAL wants.
 */
object Mp3AudioStreamProvider : AudioStreamProvider {

    override val id = "mp3"

    override fun matches(header: ByteArray): Boolean {
        // ID3v2-tagged mp3 files start with the literal "ID3".
        if (header.size >= 3 &&
            header[0] == 'I'.code.toByte() &&
            header[1] == 'D'.code.toByte() &&
            header[2] == '3'.code.toByte()
        ) {
            return true
        }
        // Otherwise the file starts straight with an MPEG audio frame: 11 sync bits set.
        return header.size >= 2 &&
            header[0] == 0xFF.toByte() &&
            (header[1].toInt() and 0xE0) == 0xE0
    }

    override fun create(input: InputStream): RecordAudioStream = Mp3AudioStream(input)

    private class Mp3AudioStream(input: InputStream) : RecordAudioStream {

        private val bitstream = Bitstream(input)
        private val decoder = Decoder()
        private var decodedFormat: AudioFormat? = null

        override val format: AudioFormat
            get() = decodedFormat
                ?: throw IllegalStateException("readAll() must be called before the format is known")

        @Throws(IOException::class)
        override fun readAll(): ByteBuffer {
            val chunks = ArrayList<ShortArray>()
            var totalShorts = 0
            var sampleRate = -1
            var channels = -1

            try {
                var header: Header? = bitstream.readFrame()
                while (header != null) {
                    val output = decoder.decodeFrame(header, bitstream) as SampleBuffer
                    if (sampleRate < 0) {
                        sampleRate = output.sampleFrequency
                        channels = output.channelCount
                    }

                    val length = output.bufferLength
                    chunks.add(output.buffer.copyOf(length))
                    totalShorts += length

                    bitstream.closeFrame()
                    header = bitstream.readFrame()
                }
            } catch (e: BitstreamException) {
                throw IOException("Failed to read MP3 stream", e)
            } catch (e: DecoderException) {
                throw IOException("Failed to decode MP3 stream", e)
            }

            if (sampleRate < 0 || channels < 1) {
                throw IOException("MP3 stream contained no decodable audio frames")
            }

            // Signed 16-bit, little-endian (native order) PCM — the layout OpenAL expects.
            decodedFormat = AudioFormat(sampleRate.toFloat(), 16, channels, true, false)

            val buffer = BufferUtils.createByteBuffer(totalShorts * 2)
            for (chunk in chunks) {
                for (sample in chunk) {
                    buffer.putShort(sample)
                }
            }
            buffer.flip()
            return buffer
        }

        override fun close() {
            try {
                bitstream.close()
            } catch (e: BitstreamException) {
                throw IOException("Failed to close MP3 stream", e)
            }
        }
    }
}
