package com.hemogoblins.betterrecords.api.client.sound

import java.io.IOException
import java.io.InputStream

interface AudioStreamProvider {

    /** Short, lowercase identifier for the format, e.g. `"ogg"` or `"mp3"`. */
    val id: String

    /**
     * Whether this provider can decode a stream that begins with [header].
     *
     * [header] holds the first few bytes of the stream (see [AudioStreamRegistry.HEADER_LENGTH]);
     * Implementations should sniff magic bytes to determine format.
     */
    fun matches(header: ByteArray): Boolean

    /**
     * Wraps [input] in a [RecordAudioStream] that decodes it to PCM.
     */
    @Throws(IOException::class)
    fun create(input: InputStream): RecordAudioStream
}
