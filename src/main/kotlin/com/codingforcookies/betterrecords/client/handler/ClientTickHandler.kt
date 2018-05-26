package com.codingforcookies.betterrecords.client.handler

import com.codingforcookies.betterrecords.ID
import com.codingforcookies.betterrecords.client.sound.SoundHandler
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import net.minecraftforge.fml.relauncher.Side

@Mod.EventBusSubscriber(modid = ID, value = [Side.CLIENT])
object ClientTickHandler {

    @SubscribeEvent
    fun incrementNowPlayingInt(event: TickEvent.ClientTickEvent) {
        if (event.phase == TickEvent.Phase.START) {
            if (SoundHandler.nowPlaying != "" && SoundHandler.nowPlayingEnd < System.currentTimeMillis()) {
                SoundHandler.nowPlaying = ""
            } else {
                SoundHandler.nowPlayingInt += 3
            }
        }
    }
}
