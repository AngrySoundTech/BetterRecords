package com.hemogoblins.betterrecords.network.clientbound

import com.hemogoblins.betterrecords.capability.ModCapabilities
import com.hemogoblins.betterrecords.client.sound.RecordSoundManager
import net.minecraft.core.BlockPos
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.RecordItem
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.fml.DistExecutor
import net.minecraftforge.network.NetworkEvent.Context
import java.util.function.Supplier

/**
 * This packet is sent to the client when a record is inserted into a player
 */
class PlayRecordPacket(
    val pos: BlockPos,
    val source: RecordSource,
) {

    fun encode(buf: FriendlyByteBuf) {
        buf.writeBlockPos(pos)
        source.encode(buf)
    }

    companion object {
        fun handle(msg: PlayRecordPacket, ctx: Supplier<Context>) {
            ctx.get().enqueueWork {
                DistExecutor.unsafeRunWhenOn(Dist.CLIENT) {
                    Runnable { RecordSoundManager.play(msg.pos, msg.source) }
                }
            }
            ctx.get().packetHandled = true
        }

        fun decode(buf: FriendlyByteBuf): PlayRecordPacket {
            return PlayRecordPacket(
                buf.readBlockPos(),
                RecordSource.decode(buf),
            )
        }
    }

    sealed class RecordSource {

        abstract fun encode(buf: FriendlyByteBuf)

        /** A BetterRecords record  */
        data class File(val name: String, val url: String, val checksum: String) : RecordSource() {
            override fun encode(buf: FriendlyByteBuf) {
                buf.writeByte(Type.FILE.ordinal)
                buf.writeUtf(name)
                buf.writeUtf(url)
                buf.writeUtf(checksum)
            }
        }

        /** A vanilla music disc  */
        data class Vanilla(val sound: ResourceLocation) : RecordSource() {
            override fun encode(buf: FriendlyByteBuf) {
                buf.writeByte(Type.VANILLA.ordinal)
                buf.writeResourceLocation(sound)
            }
        }

        private enum class Type { FILE, VANILLA }

        companion object {
            fun decode(buf: FriendlyByteBuf): RecordSource =
                when (Type.values()[buf.readByte().toInt()]) {
                    Type.FILE -> File(buf.readUtf(), buf.readUtf(), buf.readUtf())
                    Type.VANILLA -> Vanilla(buf.readResourceLocation())
                }

            fun of(stack: ItemStack): RecordSource? {
                val holder = stack.getCapability(ModCapabilities.MUSIC_HOLDER_CAPABILITY).resolve()
                if (holder.isPresent) {
                    val music = holder.get()
                    return if (music.checksum.isNotEmpty()) File(music.songName, music.url, music.checksum) else null
                }

                (stack.item as? RecordItem)?.let { return Vanilla(it.sound.location) }
                return null
            }
        }
    }
}
