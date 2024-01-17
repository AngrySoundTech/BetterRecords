package com.hemogoblins.betterrecords.client.cache

import com.hemogoblins.betterrecords.BetterRecords
import com.hemogoblins.betterrecords.api.client.CacheEntry
import com.hemogoblins.betterrecords.api.client.MusicCache
import org.apache.commons.codec.digest.DigestUtils
import java.io.File
import java.util.UUID

class FilesystemCache(
    private val tempDirectory: File,
    private val cacheDirectory: File,
) : MusicCache {

    override fun get(url: String, checksum: String, progress: (progressPercent: Int) -> Unit): CacheEntry {
        // If a checksum was provided, and it matches an entry in the cache,
        // We can skip the entire download process and just return that file.
        val cachedFile = cacheDirectory.listFiles().firstOrNull { it.name == checksum }
        if (cachedFile != null) {
            BetterRecords.logger.info("Cache Hit for $url: $checksum")
            return CacheEntry(checksum, cachedFile)
        }

        // If a checksum was not provided, or it was not in the cache,
        // We need to download the file no matter what.
        BetterRecords.logger.info("Cache Miss for $url")
        return get(url, progress)
    }

    override fun get(url: String, progress: (progressPercent: Int) -> Unit): CacheEntry {
        // Using a UUID ensures there are no collisions for the current download
        val tempName = UUID.randomUUID().toString()
        var lastReported = -1
        val file = downloadFile(url, File(tempDirectory, tempName)) { bytesRead, bytesTotal, complete ->
            val percentage = ((bytesRead * 100) / bytesTotal).toInt()

            if (percentage % 10 == 0 && percentage != lastReported) {
                BetterRecords.logger.info("Downloading $url as $tempName: $percentage%")
                lastReported = percentage
            }

            progress(percentage)
        }

        val newChecksum = DigestUtils.md5Hex(file.readBytes())
        val targetFile = File(cacheDirectory, newChecksum)

        targetFile.delete()
        file.copyTo(targetFile)
        file.delete()
        BetterRecords.logger.info("$url downloaded and moved to cache: $newChecksum")

        return CacheEntry(newChecksum, targetFile)
    }
}
