package com.hemogoblins.betterrecords.api.client

import java.io.File

/**
 * Simple object to represent a file in the cache, with extra information
 */
data class CacheEntry(
    val checksum: String,
    val file: File,
)

/**
 * The cache is responsible for managing the download and hashing of
 * music files. Files may be requested through the cache, and barring any errors
 *
 * Note: Currently we only return the checksum string, and expect
 * the caller to read the file themselves.
 * This may change in the future to wrap it in some sort of entry object.
 */
interface MusicCache {

    /**
     * Request a file through the cache.
     *
     * If the checksum is not provided, the file will
     * always attempt to be downloaded and moved to the cache.
     *
     * If the checksum is provided, it will return the checksum immediately,
     * indicating no other work needs to be done
     */
    @Throws(Exception::class) // TODO
    @Deprecated("This is NOT stable yet, and may change")
    fun get(url: String, checksum: String, progress: (progressPercent: Int) -> Unit): CacheEntry

    @Throws(Exception::class) // TODO
    @Deprecated("This is NOT stable yet, and may change")
    fun get(url: String, progress: (progressPercent: Int) -> Unit): CacheEntry

}
