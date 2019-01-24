package tech.feldman.betterrecords.item

import tech.feldman.betterrecords.api.connection.RecordConnection
import tech.feldman.betterrecords.api.wire.IRecordWire
import tech.feldman.betterrecords.api.wire.IRecordWireHome
import tech.feldman.betterrecords.api.wire.IRecordWireManipulator
import tech.feldman.betterrecords.helper.ConnectionHelper
import tech.feldman.betterrecords.network.PacketHandler
import tech.feldman.betterrecords.network.PacketWireConnection
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumActionResult
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

class ItemWire(name: String) : ModItem(name), IRecordWireManipulator {

    companion object {
        var connection: RecordConnection? = null
    }

    override fun onItemUse(player: EntityPlayer, world: World, pos: BlockPos, hand: EnumHand, facing: EnumFacing, hitX: Float, hitY: Float, hitZ: Float): EnumActionResult {
        if (!world.isRemote) {
            return EnumActionResult.PASS
        }

        (world.getTileEntity(pos) as? IRecordWire)?.let { te ->
            connection?.let {

                val x1 = -(pos.x - if (it.fromHome) it.x1 else it.x2).toFloat()
                val y1 = -(pos.y - if (it.fromHome) it.y1 else it.y2).toFloat()
                val z1 = -(pos.z - if (it.fromHome) it.z1 else it.z2).toFloat()

                if (Math.sqrt(Math.pow(x1.toDouble(), 2.toDouble()) + Math.pow(y1.toDouble(), 2.toDouble()) + Math.pow(z1.toDouble(), 2.toDouble())) > 7 || it.sameInitial(pos.x, pos.y, pos.z)) {
                    connection = null
                    return EnumActionResult.PASS
                }

                if (!it.fromHome) {
                    it.setConnection1(pos.x, pos.y, pos.z)
                } else {
                    it.setConnection2(pos.x, pos.y, pos.z)
                }

                val te1 = world.getTileEntity(BlockPos(it.x1, it.y1, it.z1))
                val te2 = world.getTileEntity(BlockPos(it.x2, it.y2, it.z2))

                if (te2 is IRecordWire) {
                    if (!(te1 is IRecordWireHome && te2 is IRecordWireHome)) {
                        ConnectionHelper.addConnection((te as TileEntity).world, te1 as IRecordWire, connection!!, world.getBlockState(te.pos))
                        ConnectionHelper.addConnection((te as TileEntity).world, te2 as IRecordWire, connection!!, world.getBlockState(te.pos))
                        PacketHandler.sendToServer(PacketWireConnection(connection!!))
                        player.getHeldItem(hand).count--
                    }
                }

                connection = null
                return EnumActionResult.PASS
            }

            connection = RecordConnection(pos.x, pos.y, pos.z, te is IRecordWireHome)
        }

        return EnumActionResult.PASS
    }
}
