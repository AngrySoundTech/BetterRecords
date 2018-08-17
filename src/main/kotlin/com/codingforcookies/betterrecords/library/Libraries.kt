package com.codingforcookies.betterrecords.library

import com.codingforcookies.betterrecords.BetterRecords
import com.codingforcookies.betterrecords.ID
import com.codingforcookies.betterrecords.ModConfig
import com.codingforcookies.betterrecords.network.PacketHandler
import com.codingforcookies.betterrecords.network.PacketSendLibrary
import com.codingforcookies.betterrecords.util.BetterUtils
import net.minecraft.client.Minecraft
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.server.MinecraftServer
import net.minecraftforge.fml.common.FMLCommonHandler
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.PlayerEvent
import net.minecraftforge.fml.common.network.FMLNetworkEvent
import net.minecraftforge.fml.relauncher.Side
import java.io.File
import java.net.URL

/**
 * Collection of all Libraries containing the songs / radio stations
 * that are available on the client or server
 *
 * Sources include:
 *  - Local library files
 *  - Remote library files
 *
 *  Local library files are loaded from  $minecraft/betterrecords/library/
 *
 * If we are running on the server, entries in this library will be sent to the client.
 * If we are running on the client, entries from the server will be added to this library.
 */
@Mod.EventBusSubscriber(Side.SERVER, modid = ID)
object Libraries {

    /** The directory where local libraries are stored */
    private val LOCAL_LIBRARY_DIR = File(when (FMLCommonHandler.instance().side) {
        Side.CLIENT -> Minecraft.getMinecraft().mcDataDir
        Side.SERVER -> File(".")
    }, "betterrecords/library")

    /** All of the library files that have been loaded */
    val libraries = mutableListOf<Library>()

    /**
     * Entry point for loading the library
     * Load both local and remote files
     */
    fun init() {
        /*  Create the library folder if it doesn't exist */
        LOCAL_LIBRARY_DIR.mkdirs()

        // Create the remoteLibraries file if it doesn't exist.
        val remoteLibrariesFile = File(LOCAL_LIBRARY_DIR.parent, "remoteLibraries.txt")
        with (remoteLibrariesFile) {
            if (!exists()) {
                writeText(BetterUtils.getResourceFromJar("assets/betterrecords/libraries/remoteLibraries.txt").readText())
            }
        }

        // Create an empty library for their etchings if it doesn't exist. We need at least one library.
        with (File(LOCAL_LIBRARY_DIR, "myEtchings.json")) {
            if (!exists()) {
                writeText(BetterUtils.getResourceFromJar("assets/betterrecords/libraries/empty_library.json").readText())
            }
        }

        // Load the mod's built in libraries
        if (ModConfig.client.loadDefaultLibraries) {
            listOf("assets/betterrecords/libraries/kevin_macleod.json")
                    .map { RemoteLibrary(BetterUtils.getResourceFromJar(it)) }
                    .forEach { libraries.add(it) }
        }

        // Load all of the local library files
        LOCAL_LIBRARY_DIR
                .listFiles()
                .map { LocalLibrary(it) }
                .forEach { libraries.add(it) }

        if (ModConfig.useRemoteLibraries) {
            // Load remote libraries
            remoteLibrariesFile
                    .readLines()
                    .map(String::trim)
                    .filter { !it.startsWith("#") }
                    .map { RemoteLibrary(URL(it)) }
                    .forEach { libraries.add(it) }
        }
    }

    @SubscribeEvent
    fun onClientConnect(event: PlayerEvent.PlayerLoggedInEvent) {
        libraries.forEach {
            BetterRecords.logger.info("Sending library \"${it.name}\" to client: ${event.player.name}")

            // We can safely cast the player because this event should only be listened to on the server
            PacketHandler.sendToPlayer(PacketSendLibrary(it), event.player as EntityPlayerMP)
        }
    }
}
