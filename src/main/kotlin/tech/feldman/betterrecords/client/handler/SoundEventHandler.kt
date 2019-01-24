package tech.feldman.betterrecords.client.handler

import tech.feldman.betterrecords.ID
import tech.feldman.betterrecords.api.event.RadioInsertEvent
import tech.feldman.betterrecords.api.event.RecordInsertEvent
import tech.feldman.betterrecords.api.event.SoundStopEvent
import tech.feldman.betterrecords.client.sound.SoundManager
import tech.feldman.betterrecords.extensions.distanceTo
import net.minecraft.client.Minecraft
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.eventhandler.EventPriority
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.relauncher.Side

@Mod.EventBusSubscriber(modid = ID, value = [Side.CLIENT])
object SoundEventHandler {

    @SubscribeEvent(priority = EventPriority.LOWEST)
    fun onRecordInserted(event: RecordInsertEvent) {
        val (pos, dimension, playRadius, sounds, repeat) = event
        val player = Minecraft.getMinecraft().player

        if (playRadius > 100000 || player.position.distanceTo(pos) < playRadius) {
            SoundManager.queueSongsAt(pos, dimension, sounds, repeat = repeat)
        }

    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    fun onRadioInserted(event: RadioInsertEvent) {
        val (pos, dimension, playRadius, sound) = event
        val player = Minecraft.getMinecraft().player

        if (playRadius > 100000 || player.position.distanceTo(pos) < playRadius) {
            SoundManager.queueStreamAt(pos, dimension, sound)
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    fun onSoundStopped(event: SoundStopEvent) {
        val (pos, dimension) = event

        SoundManager.stopQueueAt(pos, dimension)
    }
}
