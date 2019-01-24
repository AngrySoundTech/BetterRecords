package tech.feldman.betterrecords

import tech.feldman.betterrecords.handler.GuiHandler
import tech.feldman.betterrecords.library.Libraries
import tech.feldman.betterrecords.network.PacketHandler
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.minecraftforge.fml.common.network.NetworkRegistry

open class CommonProxy {

    open fun preInit(event: FMLPreInitializationEvent) {
        PacketHandler.init()
    }

    open fun init(event: FMLInitializationEvent) {
        NetworkRegistry.INSTANCE.registerGuiHandler(tech.feldman.betterrecords.BetterRecords, GuiHandler())

        Libraries.init()
    }

    open fun postInit(event: FMLPostInitializationEvent) {

    }
}
