package com.codingforcookies.betterrecords.api.sound

data class Sound(
        val url: String,
        val name: String,
        val size: Int = -1,
        val author: String = ""
)
