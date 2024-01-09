package com.hemogoblins.betterrecords.capability

import com.hemogoblins.betterrecords.api.capability.IColorable
import net.minecraftforge.common.capabilities.CapabilityManager
import net.minecraftforge.common.capabilities.CapabilityToken
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent
import net.minecraftforge.eventbus.api.IEventBus
import net.minecraftforge.eventbus.api.SubscribeEvent

object ModCapabilities {

    val COLORABLE_CAPABILITY = CapabilityManager.get(object : CapabilityToken<IColorable>() {})

    @SubscribeEvent
    fun registerCapabilities(event: RegisterCapabilitiesEvent) {
        event.register(IColorable::class.java)
    }

    fun register(eventBus: IEventBus) {
        eventBus.register(this)
    }
}