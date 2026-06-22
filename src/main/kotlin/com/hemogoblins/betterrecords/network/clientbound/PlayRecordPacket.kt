package com.hemogoblins.betterrecords.network.clientbound

import com.hemogoblins.betterrecords.client.sound.RecordSoundManager
import net.minecraft.core.BlockPos
import net.minecraft.network.FriendlyByteBuf
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.fml.DistExecutor
import net.minecraftforge.network.NetworkEvent.Context
import java.util.function.Supplier

/**
 * This packet is sent to the client when a record is inserted into a player
 */
class PlayRecordPacket(
    val pos: BlockPos,
    val checksum: String,
) {

    fun encode(buf: FriendlyByteBuf) {
        buf.writeBlockPos(pos)
        buf.writeUtf(checksum)
    }

    companion object {
        fun handle(msg: PlayRecordPacket, ctx: Supplier<Context>) {
            ctx.get().enqueueWork {
                DistExecutor.unsafeRunWhenOn(Dist.CLIENT) {
                    Runnable { RecordSoundManager.play(msg.pos, msg.checksum) }
                }
            }
            ctx.get().packetHandled = true
        }

        fun decode(buf: FriendlyByteBuf): PlayRecordPacket {
            return PlayRecordPacket(
                buf.readBlockPos(),
                buf.readUtf(),
            )
        }
    }
}
