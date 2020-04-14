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

import tech.feldman.betterrecords.api.sound.IColorableSoundHolder
import tech.feldman.betterrecords.api.sound.Sound
import tech.feldman.betterrecords.block.tile.TileFrequencyTuner
import tech.feldman.betterrecords.block.tile.TileRecordEtcher
import tech.feldman.betterrecords.extensions.readBlockPos
import tech.feldman.betterrecords.extensions.writeBlockPos
import tech.feldman.betterrecords.helper.nbt.addSound
import tech.feldman.betterrecords.helper.nbt.setColor
import io.netty.buffer.ByteBuf
import net.minecraft.item.ItemStack
import net.minecraft.util.math.BlockPos
import net.minecraftforge.fml.common.network.ByteBufUtils
import net.minecraftforge.fml.common.network.simpleimpl.IMessage
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext

class PacketURLWrite @JvmOverloads constructor(
        var pos: BlockPos = BlockPos(0, 0, 0),
        var size: Int = -1,
        var name: String = "",
        var url: String = "",
        // Color and author can actually be
        // considered optional
        var color: Int = -1,
        var author: String = ""
) : IMessage {

    override fun toBytes(buf: ByteBuf) {
        writeBlockPos(buf, pos)

        buf.writeInt(size)
        ByteBufUtils.writeUTF8String(buf, name)
        ByteBufUtils.writeUTF8String(buf, url)

        buf.writeInt(color)
        ByteBufUtils.writeUTF8String(buf, author)
    }

    override fun fromBytes(buf: ByteBuf) {
        pos = readBlockPos(buf)

        size = buf.readInt()
        name = ByteBufUtils.readUTF8String(buf)
        url = ByteBufUtils.readUTF8String(buf)

        color = buf.readInt()
        author = ByteBufUtils.readUTF8String(buf)

    }

    class Handler : IMessageHandler<PacketURLWrite, IMessage> {

        override fun onMessage(message: PacketURLWrite, ctx: MessageContext): IMessage? {
            val player = ctx.serverHandler.player
            val te = player.world.getTileEntity(message.pos)

            val itemStack: ItemStack = when (te) {
                is TileRecordEtcher -> te.record
                is TileFrequencyTuner -> te.crystal
                else -> return null
            }

            addSound(itemStack, Sound(
                    url = message.url,
                    name = message.name,
                    size = message.size,
                    author = message.author
            ))

            setColor(itemStack, message.color)

            return null
        }
    }
}
