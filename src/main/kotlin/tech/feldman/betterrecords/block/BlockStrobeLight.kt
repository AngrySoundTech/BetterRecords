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
package tech.feldman.betterrecords.block

import tech.feldman.betterrecords.api.record.IRecordAmplitude
import tech.feldman.betterrecords.api.wire.IRecordWire
import tech.feldman.betterrecords.block.tile.TileStrobeLight
import tech.feldman.betterrecords.client.render.RenderStrobeLight
import tech.feldman.betterrecords.helper.ConnectionHelper
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World

class BlockStrobeLight(name: String) : ModBlock(Material.WOOD, name), TESRProvider<TileStrobeLight>, ItemModelProvider {

    init {
        setHardness(2.75f)
        setResistance(4f)
    }

    override fun getTileEntityClass() = TileStrobeLight::class
    override fun getRenderClass() = RenderStrobeLight::class

    override fun getBoundingBox(state: IBlockState?, block: IBlockAccess?, pos: BlockPos?): AxisAlignedBB {
        return AxisAlignedBB(0.25, 0.0, 0.25, 0.75, 0.75, 0.74)
    }

    override fun getLightValue(state: IBlockState, access: IBlockAccess, pos: BlockPos): Int {
        val te = access.getTileEntity(pos)
        if (te == null || te !is IRecordWire || te !is IRecordAmplitude) return 0
        return if ((te as IRecordWire).connections.size > 0) 5 else 0
    }

    override fun onBlockAdded(world: World?, pos: BlockPos?, state: IBlockState?) {
        super.onBlockAdded(world, pos, state)
        world!!.notifyBlockUpdate(pos!!, state!!, state, 3)
    }

    override fun removedByPlayer(state: IBlockState, world: World, pos: net.minecraft.util.math.BlockPos, player: EntityPlayer, willHarvest: Boolean): Boolean {
        if (world.isRemote) return super.removedByPlayer(state, world, pos, player, willHarvest)
        val te = world.getTileEntity(pos)
        if (te != null && te is IRecordWire) ConnectionHelper.clearConnections(world, te as IRecordWire)
        return super.removedByPlayer(state, world, pos, player, willHarvest)
    }
}
