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

import tech.feldman.betterrecords.api.event.RecordInsertEvent
import tech.feldman.betterrecords.api.sound.Sound
import tech.feldman.betterrecords.helper.nbt.getSounds
import tech.feldman.betterrecords.item.ModItems
import io.netty.buffer.ByteBuf
import net.minecraft.item.ItemStack
import net.minecraft.util.math.BlockPos
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.network.ByteBufUtils
import net.minecraftforge.fml.common.network.simpleimpl.IMessage
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext

class PacketRecordPlay @JvmOverloads constructor(
        var pos: BlockPos = BlockPos(0, 0, 0),
        var dimension: Int = -1,
        var playRadius: Float = -1F,
        var repeat: Boolean = false,
        var shuffle: Boolean = false,
        var recordStack: ItemStack = ItemStack(ModItems.itemRecord)
) : IMessage {

    val sounds = mutableListOf<Sound>()

    init {
        sounds.addAll(getSounds(recordStack))
    }

    override fun toBytes(buf: ByteBuf) {
        buf.writeInt(pos.x)
        buf.writeInt(pos.y)
        buf.writeInt(pos.z)

        buf.writeInt(dimension)

        buf.writeFloat(playRadius)

        // Write the amount of sounds we're going to send,
        // to rebuild on the other side.
        buf.writeInt(sounds.size)

        sounds.forEach {
            ByteBufUtils.writeUTF8String(buf, it.url)
            ByteBufUtils.writeUTF8String(buf, it.name)
            buf.writeInt(it.size)
            ByteBufUtils.writeUTF8String(buf, it.author)
        }

        buf.writeBoolean(repeat)
        buf.writeBoolean(shuffle)
    }

    override fun fromBytes(buf: ByteBuf) {
        pos = BlockPos(buf.readInt(), buf.readInt(), buf.readInt())

        dimension = buf.readInt()

        playRadius = buf.readFloat()

        val amount = buf.readInt()
        for (i in 1..amount) {
            sounds.add(Sound(
                    ByteBufUtils.readUTF8String(buf),
                    ByteBufUtils.readUTF8String(buf),
                    buf.readInt(),
                    ByteBufUtils.readUTF8String(buf)
            ))
        }

        repeat = buf.readBoolean()
        shuffle = buf.readBoolean()
    }

    class Handler : IMessageHandler<PacketRecordPlay, IMessage> {

        override fun onMessage(message: PacketRecordPlay, ctx: MessageContext): IMessage? {
            with(message) {
                if (shuffle) {
                    sounds.shuffle()
                }

                MinecraftForge.EVENT_BUS.post(RecordInsertEvent(pos, dimension, playRadius, sounds, repeat))
            }

            return null
        }

    }
}
