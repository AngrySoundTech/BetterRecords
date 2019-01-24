package tech.feldman.betterrecords.network

import tech.feldman.betterrecords.BetterRecords
import tech.feldman.betterrecords.ModConfig
import tech.feldman.betterrecords.library.Libraries
import tech.feldman.betterrecords.library.Library
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
                        tech.feldman.betterrecords.BetterRecords.logger.info("Received library from server: ${it.name}")

                        Libraries.libraries.add(it)
                    }
                }
            }

            return null
        }
    }
}
