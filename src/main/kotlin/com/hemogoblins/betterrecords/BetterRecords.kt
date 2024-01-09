package com.hemogoblins.betterrecords

import com.hemogoblins.betterrecords.block.ModBlocks
import com.hemogoblins.betterrecords.capability.ModCapabilities
import com.hemogoblins.betterrecords.item.ModItems
import net.minecraftforge.fml.common.Mod
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