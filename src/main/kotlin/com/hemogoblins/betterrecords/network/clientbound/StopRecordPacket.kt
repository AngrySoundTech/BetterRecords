package com.hemogoblins.betterrecords.network.clientbound

import com.hemogoblins.betterrecords.client.sound.RecordSoundManager
import net.minecraft.core.BlockPos
import net.minecraft.network.FriendlyByteBuf
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.fml.DistExecutor
import net.minecraftforge.network.NetworkEvent.Context
import java.util.function.Supplier

/**
 * This packet is sent to the client when a record is removed from a player
 */
class StopRecordPacket(
    val pos: BlockPos,
) {

    fun encode(buf: FriendlyByteBuf) {
        buf.writeBlockPos(pos)
    }

    companion object {
        fun handle(msg: StopRecordPacket, ctx: Supplier<Context>) {
            ctx.get().enqueueWork {
                DistExecutor.unsafeRunWhenOn(Dist.CLIENT) {
                    Runnable { RecordSoundManager.stop(msg.pos) }
                }
            }
            ctx.get().packetHandled = true
        }

        fun decode(buf: FriendlyByteBuf): StopRecordPacket {
            return StopRecordPacket(
                buf.readBlockPos(),
            )
        }
    }
}
