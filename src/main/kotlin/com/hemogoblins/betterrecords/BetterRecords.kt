package com.hemogoblins.betterrecords

import com.hemogoblins.betterrecords.block.ModBlocks
import com.hemogoblins.betterrecords.capability.ModCapabilities
import com.hemogoblins.betterrecords.client.screen.ModScreens
import com.hemogoblins.betterrecords.item.ModItems
import com.hemogoblins.betterrecords.menu.ModMenuTypes
import net.minecraftforge.fml.ModLoadingContext
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.config.ModConfig
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent
import net.minecraftforge.fml.event.lifecycle.FMLDedicatedServerSetupEvent
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import thedarkcolour.kotlinforforge.forge.MOD_BUS
import thedarkcolour.kotlinforforge.forge.runForDist

/**
 * The main entrypoint for the mod, this class handles initialization, grabs the event bus,
 * and delegates registration to any of our registries. Additionally, it contains a place
 * for us to hook in sided behavior in the future, such as record library initialization.
 */
@Mod(BetterRecords.ID)
object BetterRecords {

    /** ID of the mod. Pretty much everything uses this for registry/localization keys, so this should never change */
    const val ID = "betterrecords"

    /** Global logger instance for the mod */
    val logger: Logger = LogManager.getLogger(ID)

    init {
        logger.info("Hello, World!")

        ModBlocks.register(MOD_BUS)
        ModItems.register(MOD_BUS)
        ModCapabilities.register(MOD_BUS)
        ModMenuTypes.register(MOD_BUS)
        ModScreens.register(MOD_BUS)

        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, BRConfig.CLIENT_SPEC)
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, BRConfig.COMMON_SPEC)
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, BRConfig.SERVER_SPEC)

        runForDist(
                clientTarget = {
                    MOD_BUS.addListener(::onClientSetup)
                },
                serverTarget = {
                    MOD_BUS.addListener(::onServerSetup)
                }
        )
    }

    /**
     * Client side setup events
     */
    private fun onClientSetup(event: FMLClientSetupEvent) {
    }

    /**
     * Server side setup events
     */
    private fun onServerSetup(event: FMLDedicatedServerSetupEvent) {

    }
}