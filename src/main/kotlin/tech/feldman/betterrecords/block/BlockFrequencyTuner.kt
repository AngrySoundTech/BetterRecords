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

import tech.feldman.betterrecords.BetterRecords
import tech.feldman.betterrecords.block.tile.TileFrequencyTuner
import tech.feldman.betterrecords.client.render.RenderFrequencyTuner
import net.minecraft.block.Block
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.item.EntityItem
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World
import java.util.*

class BlockFrequencyTuner(name: String) : ModBlockDirectional(Material.WOOD, name), TESRProvider<TileFrequencyTuner>, ItemModelProvider {

    init {
        setHardness(1.5f)
        setResistance(5.5f)
    }

    override fun getTileEntityClass() = TileFrequencyTuner::class
    override fun getRenderClass() = RenderFrequencyTuner::class

    override fun onBlockAdded(world: World, pos: BlockPos, state: IBlockState) =
            world.notifyBlockUpdate(pos, state, state, 3)

    override fun getBoundingBox(state: IBlockState, block: IBlockAccess, pos: BlockPos) = when (getMetaFromState(state)) {
        0, 2 -> AxisAlignedBB(.18, 0.0, .12, .82, .6, .88)
        1, 3 -> AxisAlignedBB(.12, 0.0, .18, .88, .6, .82)
        else -> Block.FULL_BLOCK_AABB
    }

    override fun onBlockActivated(world: World, pos: BlockPos, state: IBlockState, player: EntityPlayer, hand: EnumHand, side: EnumFacing, hitX: Float, hitY: Float, hitZ: Float): Boolean {
        (world.getTileEntity(pos) as? TileFrequencyTuner)?.let {
            player.openGui(tech.feldman.betterrecords.BetterRecords, 1, world, pos.x, pos.y, pos.z)
            return true
        }
        return false
    }

    override fun breakBlock(world: World, pos: BlockPos, state: IBlockState) {
        dropItem(world, pos)
        super.breakBlock(world, pos, state)
    }

    private fun dropItem(world: World, pos: BlockPos) {
        (world.getTileEntity(pos) as? TileFrequencyTuner)?.let { te ->
            if (!te.crystal.isEmpty) {
                val random = Random()
                val rx = random.nextDouble() * 0.8F + 0.1F
                val ry = random.nextDouble() * 0.8F + 0.1F
                val rz = random.nextDouble() * 0.8F + 0.1F

                val entityItem = EntityItem(world, pos.x + rx, pos.y + ry, pos.z + rz, ItemStack(te.crystal.item, te.crystal.count, te.crystal.itemDamage))
                if (te.crystal.hasTagCompound()) entityItem.item.tagCompound = te.crystal.tagCompound!!.copy()
                entityItem.motionX = random.nextGaussian() * 0.05F
                entityItem.motionY = random.nextGaussian() * 0.05F + 0.2F
                entityItem.motionZ = random.nextGaussian() * 0.05F

                world.spawnEntity(entityItem)
                te.crystal.count = 0
                te.crystal = ItemStack.EMPTY
            }
        }
    }
}
