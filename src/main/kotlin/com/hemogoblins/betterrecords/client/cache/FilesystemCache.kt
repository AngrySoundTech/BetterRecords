package com.hemogoblins.betterrecords.client.cache

import com.hemogoblins.betterrecords.api.client.MusicCache
import java.nio.file.Path

class FilesystemCache(
    val tempDirectory: Path,
    val directory: Path,
) : MusicCache {

    override fun get(url: String, checksum: String?): String {
        println("GET $url, $tempDirectory, $directory")

        return ""
    }
}