package com.codingforcookies.betterrecords.network

import com.codingforcookies.betterrecords.BetterRecords
import com.codingforcookies.betterrecords.ModConfig
import com.codingforcookies.betterrecords.library.Libraries
import com.codingforcookies.betterrecords.library.Library
import io.netty.buffer.ByteBuf
import net.minecraftforge.fml.common.network.ByteBufUtils
import net.minecraftforge.fml.common.network.simpleimpl.IMessage
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext

class PacketSendLibrary @JvmOverloads constructor(
        var library: Library? = null
) : IMessage {

    override fun toBytes(buf: ByteBuf) {
        ByteBufUtils.writeUTF8String(buf, library.toString())
    }

    override fun fromBytes(buf: ByteBuf) {
        library = Library.fromString(ByteBufUtils.readUTF8String(buf))
    }

    class Handler : IMessageHandler<PacketSendLibrary, IMessage> {

        override fun onMessage(message: PacketSendLibrary, ctx: MessageContext): IMessage? {
            with(message) {
                library?.let {
                    if (ModConfig.useRemoteLibraries) {
                        BetterRecords.logger.info("Received library from server: ${it.name}")

                        Libraries.libraries.add(it)
                    }
                }
            }

            return null
        }
    }
}
