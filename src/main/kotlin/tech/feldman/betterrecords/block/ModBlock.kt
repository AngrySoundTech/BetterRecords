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
import tech.feldman.betterrecords.api.BetterRecordsAPI
import net.minecraft.block.BlockContainer
import net.minecraft.block.material.Material
import net.minecraft.block.state.BlockFaceShape
import net.minecraft.block.state.IBlockState
import net.minecraft.client.renderer.block.statemap.StateMap
import net.minecraft.util.EnumBlockRenderType
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World
import net.minecraftforge.client.model.ModelLoader
import kotlin.reflect.full.createInstance

/**
 * Abstract representation of a BetterRecords block.
 *
 * Since every block in BetterRecords is a tile entity, most of the logic for registering the tile entity and TESR is
 * here. However, this class is designed such that it can still be used for a block that is not a TESR without issue.
 */
abstract class ModBlock(material: Material, open val name: String) : BlockContainer(material) {

    /**
     * Set the registry name and creative tab for the block
     */
    init {
        setRegistryName("betterrecords:$name")
        unlocalizedName = registryName.toString()

        setCreativeTab(tech.feldman.betterrecords.BetterRecords.creativeTab)
    }

    /**
     * By default, the state mapper for our block will be cardinal directions.
     * If a different state mapper is desired, this can be overridden by the parent class
     */
    open fun setStateMapper() {
        ModelLoader.setCustomStateMapper(this, StateMap.Builder().ignore(BetterRecordsAPI.CARDINAL_DIRECTIONS).build())
    }

    /**
     * TODO: Try to get this method out of this class
     */
    override fun createNewTileEntity(worldIn: World?, meta: Int) =
            if (this is TileEntityProvider<*>) {
                getTileEntityClass().createInstance()
            } else {
                null
            }

    override fun getRenderType(state: IBlockState?) = EnumBlockRenderType.ENTITYBLOCK_ANIMATED
    override fun isFullCube(state: IBlockState?) = false
    override fun isOpaqueCube(state: IBlockState?) = false
    override fun getBlockFaceShape(worldIn: IBlockAccess, state: IBlockState, pos: BlockPos, face: EnumFacing) =
            BlockFaceShape.UNDEFINED
}
