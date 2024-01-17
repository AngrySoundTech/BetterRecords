package com.hemogoblins.betterrecords

import com.hemogoblins.betterrecords.api.client.MusicCache
import com.hemogoblins.betterrecords.block.ModBlocks
import com.hemogoblins.betterrecords.block.renderer.ModRenderers
import com.hemogoblins.betterrecords.capability.ModCapabilities
import com.hemogoblins.betterrecords.client.cache.FilesystemCache
import com.hemogoblins.betterrecords.client.screen.ModScreens
import com.hemogoblins.betterrecords.item.ModItems
import com.hemogoblins.betterrecords.menu.ModMenuTypes
import com.hemogoblins.betterrecords.network.ModNetwork
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent
import net.minecraftforge.fml.event.lifecycle.FMLDedicatedServerSetupEvent
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import thedarkcolour.kotlinforforge.forge.MOD_BUS
import thedarkcolour.kotlinforforge.forge.runForDist
import java.io.File
import kotlin.io.path.Path

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

    val cache: MusicCache by lazy {
        logger.info("Initializing Cache...")
        FilesystemCache(
            File(BRConfig.Client.cacheTempDirectory.get()),
            File(BRConfig.Client.cacheDirectory.get())
        )
    }

    init {
        BRConfig.register()

        ModBlocks.register(MOD_BUS)
        ModItems.register(MOD_BUS)
        ModCapabilities.register(MOD_BUS)
        ModMenuTypes.register(MOD_BUS)
        ModScreens.register(MOD_BUS)
        ModRenderers.register(MOD_BUS)
        ModNetwork.register()

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