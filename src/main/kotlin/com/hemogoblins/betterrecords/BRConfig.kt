package com.hemogoblins.betterrecords

import net.minecraftforge.common.ForgeConfigSpec
import net.minecraftforge.common.ForgeConfigSpec.Builder
import net.minecraftforge.fml.ModLoadingContext
import net.minecraftforge.fml.config.ModConfig

object BRConfig {

    fun register() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, Client.spec)
    }

    object Client {
        val spec: ForgeConfigSpec

        // For now we only support the filesystem cache... This may break
        // config in the future
        val cacheTempDirectory: ForgeConfigSpec.ConfigValue<String>
        val cacheDirectory: ForgeConfigSpec.ConfigValue<String>

        init {
            val builder = Builder()

            builder
                .comment("Music Download Cache")
                .push("cache")

            this.cacheTempDirectory = builder
                .comment("Temporary directory for downloads")
                .define("tempDirectory", "test")

            this.cacheDirectory = builder
                .comment("Location to store cached files")
                .define("directory", "test")

            builder.pop()
            spec = builder.build()
        }
    }

    object Common {
    }

    object Server {

    }
}
