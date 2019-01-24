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

import tech.feldman.betterrecords.api.connection.RecordConnection
import tech.feldman.betterrecords.api.wire.IRecordWire
import tech.feldman.betterrecords.api.wire.IRecordWireHome
import tech.feldman.betterrecords.helper.ConnectionHelper
import io.netty.buffer.ByteBuf
import net.minecraft.util.math.BlockPos
import net.minecraftforge.fml.common.network.ByteBufUtils
import net.minecraftforge.fml.common.network.simpleimpl.IMessage
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext

class PacketWireConnection @JvmOverloads constructor(
        var connection: RecordConnection = RecordConnection(0, 0, 0, true)
) : IMessage {

    override fun toBytes(buf: ByteBuf) {
        ByteBufUtils.writeUTF8String(buf, connection.toString())
    }

    override fun fromBytes(buf: ByteBuf) {
        connection = RecordConnection(ByteBufUtils.readUTF8String(buf))
    }

    class Handler : IMessageHandler<PacketWireConnection, IMessage> {

        override fun onMessage(message: PacketWireConnection, ctx: MessageContext): IMessage? {
            val player = ctx.serverHandler.player

            with(message) {
                val te1 = player.world.getTileEntity(BlockPos(connection.x1, connection.y1, connection.z1))
                val te2 = player.world.getTileEntity(BlockPos(connection.x2, connection.y2, connection.z2))

                if (te1 is IRecordWire && te2 is IRecordWire) {
                    if (!(te1 is IRecordWireHome && te2 is IRecordWireHome)) {
                        ConnectionHelper.addConnection(player.world, te1, connection, player.world.getBlockState(te1.pos))
                        ConnectionHelper.addConnection(player.world, te2, connection, player.world.getBlockState(te2.pos))
                    }
                }
            }

            return null
        }
    }
}
