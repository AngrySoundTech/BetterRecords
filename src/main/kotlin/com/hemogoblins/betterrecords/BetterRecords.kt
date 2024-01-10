package com.hemogoblins.betterrecords

import com.hemogoblins.betterrecords.block.ModBlocks
import com.hemogoblins.betterrecords.capability.ModCapabilities
import com.hemogoblins.betterrecords.item.ModItems
import net.minecraftforge.fml.ModLoadingContext
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.config.ModConfig
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent
import net.minecraftforge.fml.event.lifecycle.FMLDedicatedServerSetupEvent
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import thedarkcolour.kotlinforforge.forge.MOD_BUS
import thedarkcolour.kotlinforforge.forge.runForDist

@Mod(BetterRecords.ID)
object BetterRecords {

    const val ID = "betterrecords"

    val logger: Logger = LogManager.getLogger(ID)

    init {
        logger.info("Hello, World!")

        ModBlocks.register(MOD_BUS)
        ModItems.register(MOD_BUS)
        ModCapabilities.register(MOD_BUS)

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

    private fun onClientSetup(event: FMLClientSetupEvent) {
    }

    private fun onServerSetup(event: FMLDedicatedServerSetupEvent) {

    }
}