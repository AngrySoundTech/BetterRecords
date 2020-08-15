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
package tech.feldman.betterrecords

import net.minecraftforge.common.config.Config
import net.minecraftforge.common.config.ConfigManager
import net.minecraftforge.fml.client.event.ConfigChangedEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

/**
 * Config file for BetterRecords
 *
 * As of right now, having this in Kotlin creates an unnecessary instance{} block in the config.
 * I don't really care.
 */
@Config(modid = ID)
@Config.LangKey("betterrecords.config.title")
object ModConfig {

    @JvmField
    @Config.Comment("Maximum speaker radius")
    @Config.RangeInt(min = -1, max = 1000)
    var maxSpeakerRadius = -1

    @JvmField
    @Config.Comment("Should the mod download libraries from the internet or the server?")
    var useRemoteLibraries = true

    @JvmField
    @Config.Comment("Client-Specific config settings")
    @Config.LangKey("betterrecords.config.client.title")
    var client = Client()

    class Client {

        @JvmField
        @Config.Comment("Enable developer mode")
        var devMode = false
        @JvmField
        @Config.Comment("Max file size to download (in megabytes)")
        @Config.RangeInt(min = 1, max = 1000)
        var downloadMax = 10

        @JvmField
        @Config.Comment("Play Songs while downloading\nFor those with fast internet!")
        var playWhileDownloading = false

        @JvmField
        @Config.RangeInt(min = 256, max = 2048)
        var streamBuffer = 1024

        @JvmField
        @Config.Comment("Should radio be streamed")
        var streamRadio = true

        @JvmField
        @Config.Comment("Intensity of lights")
        @Config.RangeInt(min = -1, max = 3)
        var flashMode = -1

        @JvmField
        @Config.Comment("Max speaker wire length")
        @Config.RangeInt(min = 5, max = 80)
        var wireLength = 7

        @JvmField
        @Config.Comment("Should the mod's built in libraries be loaded")
        var loadDefaultLibraries = true
    }

    fun update() {
        ConfigManager.sync(ID, Config.Type.INSTANCE)
    }

    @Mod.EventBusSubscriber(modid = ID)
    private object EventHandler {

        /**
         * Inject the new values and save to the config file when the config has been changed from the GUI.
         */
        @SubscribeEvent
        fun onConfigChanged(event: ConfigChangedEvent.OnConfigChangedEvent) {
            if (event.modID == ID) {
                ModConfig.update()
            }
        }
    }
}
