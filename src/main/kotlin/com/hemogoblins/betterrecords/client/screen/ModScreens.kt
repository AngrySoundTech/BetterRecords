package com.hemogoblins.betterrecords.client.screen

import com.hemogoblins.betterrecords.menu.ModMenuTypes
import net.minecraft.client.gui.screens.MenuScreens
import net.minecraftforge.eventbus.api.IEventBus
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent

object ModScreens {

    @SubscribeEvent
    fun registerScreenConstructors(event: FMLClientSetupEvent) {
        event.enqueueWork {
            MenuScreens.register(ModMenuTypes.RECORD_ETCHER_MENU.get(), ::RecordEtcherScreen)
        }
    }

    fun register(eventBus: IEventBus) {
        eventBus.register(this)
    }
}