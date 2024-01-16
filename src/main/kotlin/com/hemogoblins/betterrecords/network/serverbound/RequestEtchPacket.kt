package com.hemogoblins.betterrecords.network.serverbound

import com.hemogoblins.betterrecords.BetterRecords
import com.hemogoblins.betterrecords.block.entity.RecordEtcherBlockEntity
import com.hemogoblins.betterrecords.capability.ModCapabilities
import net.minecraft.core.BlockPos
import net.minecraft.network.FriendlyByteBuf
import net.minecraftforge.network.NetworkEvent.Context
import java.util.function.Supplier

/**
 * This packet is sent to the server when the player writes information to a record
 */
class RequestEtchPacket(
    val pos: BlockPos,
    val name: String,
) {

    fun encode(buf: FriendlyByteBuf) {
        buf.writeBlockPos(pos)
        buf.writeUtf(name)
    }

    companion object {
        fun handle(msg: RequestEtchPacket, ctx: Supplier<Context>) {
            ctx.get().enqueueWork {
                val player = ctx.get().sender!!
                val blockEntity = player.serverLevel().getBlockEntity(msg.pos)

                if (blockEntity is RecordEtcherBlockEntity) {
                    val stack = blockEntity.getSlottedRecord()
                    val cap = stack.getCapability(ModCapabilities.MUSIC_HOLDER_CAPABILITY)

                    cap.ifPresent {
                        BetterRecords.logger.info("Etching record at ${msg.pos}") // TODO: More information
                        it.songName = msg.name
                    }

                    blockEntity.triggerUpdate() // Notifies clients
                }
            }
            ctx.get().packetHandled = true
        }

        fun decode(buf: FriendlyByteBuf): RequestEtchPacket {
            return RequestEtchPacket(
                buf.readBlockPos(),
                buf.readUtf(),
            )
        }
    }
}