package com.hemogoblins.betterrecords.client.sound

import java.io.FilterInputStream
import java.io.InputStream

/**
 * Wraps a file stream tracks how much of the file has been consumed onto [sound]
 */
class ProgressInputStream(
    delegate: InputStream,
    private val total: Long,
    private val sound: RecordPlayingSound,
) : FilterInputStream(delegate) {

    private var read = 0L

    override fun read(): Int {
        val b = super.read()
        if (b >= 0) report(1)
        return b
    }

    override fun read(b: ByteArray, off: Int, len: Int): Int {
        val n = super.read(b, off, len)
        if (n > 0) report(n.toLong())
        return n
    }

    private fun report(count: Long) {
        read += count
        if (total > 0) {
            val fraction = (read.toFloat() / total).coerceIn(0f, 1f)
            if (fraction > sound.loadProgress) {
                sound.loadProgress = fraction
            }
        }
    }
}
