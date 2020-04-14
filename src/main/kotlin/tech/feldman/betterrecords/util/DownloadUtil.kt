/**
 * The MIT License
 *
 * Copyright (c) 2019 Nicholas Feldman
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package tech.feldman.betterrecords.util

import tech.feldman.betterrecords.BetterRecords
import tech.feldman.betterrecords.ModConfig
import java.io.BufferedInputStream
import java.io.File
import java.io.FileOutputStream
import java.net.URL

/**
 * Downloads a resource asynchronously.
 */
fun downloadFile(url: URL, target: File,
                 update: (curr: Float, total: Float) -> Unit = { _, _ -> },
                 success: () -> Unit = {},
                 failure: () -> Unit = {}) {

    val connection = url.openConnection()
    val size = connection.contentLength.toLong()

    if (size == target.length()) {
        tech.feldman.betterrecords.BetterRecords.logger.info("Target file exists in cache")
        success()
        return
    }

    if (size / 1024 / 1024 > ModConfig.client.downloadMax) {
        failure()
        return
    }

    // Delete the target in case it is broken / A different file in the cache for whatever reason
    target.delete()

    // Create the cache folder if it doesn't exist
    target.parentFile.mkdirs()

    val inputStream = BufferedInputStream(url.openStream())
    val outputStream = FileOutputStream(target)

    var bytesCopied = 0L
    val buffer = ByteArray(1024)
    var bytes = inputStream.read(buffer)
    while (bytes >= 0) {
        outputStream.write(buffer, 0, bytes)
        bytesCopied += bytes
        update(bytesCopied.toFloat(), size.toFloat())
        bytes = inputStream.read(buffer)
    }

    inputStream.close()
    outputStream.close()

    success()
}
