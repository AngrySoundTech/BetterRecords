package com.codingforcookies.betterrecords

import com.codingforcookies.betterrecords.handler.ConfigHandler
import com.codingforcookies.betterrecords.handler.GuiHandler
import com.codingforcookies.betterrecords.network.PacketHandler
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.minecraftforge.fml.common.network.NetworkRegistry
import com.codingforcookies.betterrecords.common.packets.PacketHandler as OldPacketHandler

open class CommonProxy {

    open fun preInit(event: FMLPreInitializationEvent) {
        ConfigHandler.loadConfig(event.suggestedConfigurationFile)

        PacketHandler.init()
    }

    open fun init(event: FMLInitializationEvent) {
        NetworkRegistry.INSTANCE.registerGuiHandler(BetterRecords, GuiHandler())
    }

    open fun postInit(event: FMLPostInitializationEvent) {

    }
}
