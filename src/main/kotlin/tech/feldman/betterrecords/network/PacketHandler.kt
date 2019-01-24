package tech.feldman.betterrecords.network

import tech.feldman.betterrecords.NETWORK_CHANNEL
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraftforge.fml.common.network.simpleimpl.IMessage
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper
import net.minecraftforge.fml.relauncher.Side.CLIENT
import net.minecraftforge.fml.relauncher.Side.SERVER

object PacketHandler {

    private val HANDLER = SimpleNetworkWrapper(NETWORK_CHANNEL)

    fun init() {
        HANDLER.registerMessage(PacketRecordPlay.Handler::class.java, PacketRecordPlay::class.java, 0, CLIENT)
        HANDLER.registerMessage(PacketRadioPlay.Handler::class.java, PacketRadioPlay::class.java, 1, CLIENT)
        HANDLER.registerMessage(PacketSoundStop.Handler::class.java, PacketSoundStop::class.java, 2, CLIENT)
        HANDLER.registerMessage(PacketWireConnection.Handler::class.java, PacketWireConnection::class.java, 3, SERVER)
        HANDLER.registerMessage(PacketURLWrite.Handler::class.java, PacketURLWrite::class.java, 4, SERVER)
        HANDLER.registerMessage(PacketSendLibrary.Handler::class.java, PacketSendLibrary::class.java, 5, CLIENT)
    }

    fun sendToAll(msg: IMessage) {
        HANDLER.sendToAll(msg)
    }

    fun sendToServer(msg: IMessage) {
        HANDLER.sendToServer(msg)
    }

    fun sendToPlayer(msg: IMessage, player: EntityPlayerMP) {
        HANDLER.sendTo(msg, player)
    }
}