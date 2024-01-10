package com.hemogoblins.betterrecords.capability

import com.hemogoblins.betterrecords.api.capability.IColorable
import com.hemogoblins.betterrecords.api.capability.IMusicHolder
import net.minecraftforge.common.capabilities.CapabilityManager
import net.minecraftforge.common.capabilities.CapabilityToken
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent
import net.minecraftforge.eventbus.api.IEventBus
import net.minecraftforge.eventbus.api.SubscribeEvent

object ModCapabilities {

    val COLORABLE_CAPABILITY = CapabilityManager.get(object : CapabilityToken<IColorable>() {})
    val MUSIC_HOLDER_CAPABILITY = CapabilityManager.get(object : CapabilityToken<IMusicHolder>() {})

    @SubscribeEvent
    fun registerCapabilities(event: RegisterCapabilitiesEvent) {
        event.register(IColorable::class.java)
        event.register(IMusicHolder::class.java)
    }

    fun register(eventBus: IEventBus) {
        eventBus.register(this)
    }
}