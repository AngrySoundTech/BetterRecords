package com.hemogoblins.betterrecords.client.cache

import java.io.BufferedInputStream
import java.io.File
import java.io.FileOutputStream
import java.lang.Exception
import java.net.URL

fun downloadFile(
    url: String,
    target: File,
    progress: (bytesRead: Long, bytesTotal: Long, complete: Boolean) -> Unit
): File {
    val source = URL(url)

    val connection = source.openConnection()
    val size = connection.contentLength.toLong()

    // TODO: Download max size
    if (false) {
        throw Exception()
    }

    // Just in case we attempted this one already, delete it.
    target.delete()

    val inputStream = BufferedInputStream(source.openStream())
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
