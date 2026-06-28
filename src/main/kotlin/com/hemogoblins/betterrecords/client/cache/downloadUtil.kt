package com.hemogoblins.betterrecords.client.cache

import java.io.BufferedInputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.lang.Exception
import java.net.URL

/**
 * Thrown when a downloaded URL responds with a content type that isn't audio (e.g. an HTML page or redirect)
 */
class UnsupportedAudioTypeException(val contentType: String, url: String) :
    IOException("Expected an audio file but $url returned content type '$contentType'")

private fun isAcceptableAudioType(contentType: String): Boolean {
    return contentType.startsWith("audio/") ||
        contentType == "application/ogg" ||
        contentType == "application/octet-stream" ||
        contentType == "binary/octet-stream"
}

fun downloadFile(
    url: String,
    target: File,
    progress: (bytesRead: Long, bytesTotal: Long, complete: Boolean) -> Unit
): File {
    val source = URL(url)

    val connection = source.openConnection()
    connection.connect()

    // Reject anything that isn't audio
    val contentType = connection.contentType?.substringBefore(';')?.trim()?.lowercase()
    if (contentType != null && !isAcceptableAudioType(contentType)) {
        throw UnsupportedAudioTypeException(contentType, url)
    }

    val size = connection.contentLengthLong

    // TODO: Download max size
    if (false) {
        throw Exception()
    }

    // Just in case we attempted this one already, delete it.
    target.delete()

    val inputStream = BufferedInputStream(connection.getInputStream())
    val outputStream = FileOutputStream(target)

    var bytesCopied = 0L
    val buffer = ByteArray(1024)
    var bytes = inputStream.read(buffer)
    while (bytes >= 0) {
        outputStream.write(buffer, 0, bytes)
        bytesCopied += bytes
        progress(bytesCopied, size, false)
        bytes = inputStream.read(buffer)
    }

    inputStream.close()
    outputStream.close()

    progress(bytesCopied, size, true)
    return target
}
