package com.hemogoblins.betterrecords.api.client.sound

import com.hemogoblins.betterrecords.BetterRecords
import java.io.IOException
import java.io.InputStream
import java.io.PushbackInputStream
import java.util.concurrent.CopyOnWriteArrayList

/**
 * Registry of [AudioStreamProvider]s, used to pick the right decoder for a cached
 * audio file at playback time.
 *
 * Detection is done by sniffing the first [HEADER_LENGTH] bytes of the stream, so
 * the format does not have to be known ahead of time
 */
object AudioStreamRegistry {

    /** Number of leading bytes handed to [AudioStreamProvider.matches] for sniffing. */
    const val HEADER_LENGTH = 4

    private val providers = CopyOnWriteArrayList<AudioStreamProvider>()

    /** Registers [provider]. Providers are checked in registration order. */
    fun register(provider: AudioStreamProvider) {
        providers.add(provider)
        BetterRecords.logger.info("Registered audio stream provider: {}", provider.id)
    }

    /** All registered providers. */
    fun providers(): List<AudioStreamProvider> = providers.toList()

    /**
     * Sniffs [input] and wraps it in a [RecordAudioStream] using the first provider that [AudioStreamProvider.matches] its header.
     *
     * @throws IOException if no registered provider supports the stream.
     */
    @Throws(IOException::class)
    fun open(input: InputStream): RecordAudioStream {
        val pushback = PushbackInputStream(input, HEADER_LENGTH)

        val header = ByteArray(HEADER_LENGTH)
        var read = 0
        while (read < HEADER_LENGTH) {
            val r = pushback.read(header, read, HEADER_LENGTH - read)
            if (r < 0) break
            read += r
        }
        if (read > 0) pushback.unread(header, 0, read)

        val sniffed = if (read == HEADER_LENGTH) header else header.copyOf(read)
        val provider = providers.firstOrNull { it.matches(sniffed) }
            ?: throw IOException(
                "No audio stream provider for header [${sniffed.joinToString(" ") { "%02X".format(it) }}]"
            )

        BetterRecords.logger.debug("Decoding record audio using '{}' provider", provider.id)
        return provider.create(pushback)
    }
}
