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
package tech.feldman.betterrecords.helper

import tech.feldman.betterrecords.api.connection.RecordConnection
import tech.feldman.betterrecords.api.wire.IRecordWire
import tech.feldman.betterrecords.api.wire.IRecordWireHome
import tech.feldman.betterrecords.item.ModItems
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.item.EntityItem
import net.minecraft.item.ItemStack
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import java.util.*
import kotlin.collections.HashMap

// TODO: Create unit tests and refactor methods.
object ConnectionHelper {

    fun serializeConnections(rec: List<RecordConnection>): String {
        if (rec.isEmpty()) {
            return ""
        }

        var ret = ""

        for (recc in rec) {
            ret += recc.toString() + "]"
        }

        return ret.substring(0, ret.length - 1)
    }

    fun unserializeConnections(rec: String): List<RecordConnection> {
        if (rec.trim() == "") {
            return listOf()
        }

        val recc = rec.split("]")
        val ret = mutableListOf<RecordConnection>()

        for (str in recc) {
            ret += RecordConnection(str)
        }

        return ret
    }

    fun serializeWireSystemInfo(wireSystemInfo: HashMap<String, Int>): String {
        if (wireSystemInfo.isEmpty()) {
            return ""
        }

        var ret = ""

        for ((key, value) in wireSystemInfo)
            ret += "$key,$value]"

        return ret.substring(0, ret.length - 1)
    }

    fun unserializeWireSystemInfo(wireSystemInfo: String): HashMap<String, Int> {
        if (wireSystemInfo.trim { it <= ' ' } == "") {
            return HashMap()
        }


        val wsi = wireSystemInfo.split("]")
        val ret = HashMap<String, Int>()

        wsi
                .map { it.split(",") }
                .forEach { ret.put(it.first(), Integer.parseInt(it[1])) }

        return ret
    }

    fun addConnection(world: World, iRecordWire: IRecordWire, rec: RecordConnection, state: IBlockState) {
        (0 until iRecordWire.connections.size)
                .filter { iRecordWire.connections[it].same(rec) }
                .forEach { return }

        iRecordWire.connections.add(rec)
        world.notifyBlockUpdate((iRecordWire as TileEntity).pos, state, state, 3)
        val te = world.getTileEntity(BlockPos(rec.x1, rec.y1, rec.z1))
        if (te != null && te is IRecordWireHome && te != iRecordWire) {
            (te as IRecordWireHome).increaseAmount(iRecordWire)
            world.notifyBlockUpdate((iRecordWire as TileEntity).pos, state, state, 3)
        }
    }

    fun removeConnection(world: World, iRecordWire: IRecordWire, rec: RecordConnection) {
        for (i in 0 until iRecordWire.connections.size)
            if (iRecordWire.connections[i].same(rec)) {
                val te = world.getTileEntity(BlockPos(iRecordWire.connections[i].x1, iRecordWire.connections[i].y1, iRecordWire.connections[i].z1))

                if (te != null && te is IRecordWireHome && te !== iRecordWire) {
                    (te as IRecordWireHome).decreaseAmount(iRecordWire)
                    val pos = (iRecordWire as TileEntity).pos
                    world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 3)
                    val rand = Random()

                    val rx = rand.nextFloat() * 0.8f + 0.1f
                    val ry = rand.nextFloat() * 0.8f + 0.1f
                    val rz = rand.nextFloat() * 0.8f + 0.1f

                    val entityItem = EntityItem(world, (te.pos.x + rx).toDouble(), (te.pos.y + ry).toDouble(), (te.pos.z + rz).toDouble(), ItemStack(ModItems.itemWire))

                    entityItem.motionX = rand.nextGaussian() * 0.05f
                    entityItem.motionY = rand.nextGaussian() * 0.05f + 0.2f
                    entityItem.motionZ = rand.nextGaussian() * 0.05f
                    world.spawnEntity(entityItem)

                    removeConnection(world, te as IRecordWire, rec)
                }

                iRecordWire.connections.removeAt(i)
                break
            }
    }

    fun clearConnections(world: World, iRecordWire: IRecordWire) {
        while (iRecordWire.connections.size != 0) {
            val connection = iRecordWire.connections[0]
            val te = world.getTileEntity(BlockPos(connection.x1, connection.y1, connection.z1))
            if (te != null && te is IRecordWire) {
                removeConnection(world, iRecordWire, connection)
                world.notifyBlockUpdate(te.pos, world.getBlockState(te.pos), world.getBlockState(te.pos), 3)
            } else {
                System.err.println("Warning on clearing connections: Attached block is not a member of IRecordWire! This may cause ghost connections until a relog!")
                iRecordWire.connections.removeAt(0)
            }
        }
        val pos = (iRecordWire as TileEntity).pos
        world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 3)
    }
}