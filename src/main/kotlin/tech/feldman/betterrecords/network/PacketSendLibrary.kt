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
