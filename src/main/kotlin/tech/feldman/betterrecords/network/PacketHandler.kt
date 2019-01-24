/**
 * The MIT License
 *
 * Copyright (c) 2019 Nicholas Feldman
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
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