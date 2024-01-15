package com.hemogoblins.betterrecords.network

import com.hemogoblins.betterrecords.BetterRecords
import com.hemogoblins.betterrecords.network.serverbound.RequestEtchPacket
import net.minecraft.resources.ResourceLocation
import net.minecraftforge.network.NetworkRegistry
import net.minecraftforge.network.simple.SimpleChannel


object ModNetwork {

    private const val PROTOCOL_VERSION = "1"

    val channel: SimpleChannel = NetworkRegistry.newSimpleChannel(
        ResourceLocation(BetterRecords.ID, "main"),
        { PROTOCOL_VERSION },
        PROTOCOL_VERSION::equals,
        PROTOCOL_VERSION:: equals,
    )

    fun register() {
        var i = 0

        // Clientbound

        // Serverbound
        channel.registerMessage(
            i++,
            RequestEtchPacket::class.java,
            RequestEtchPacket::encode,
            RequestEtchPacket::decode,
            RequestEtchPacket::handle,
        )
    }
}
