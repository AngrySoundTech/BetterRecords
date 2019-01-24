package tech.feldman.betterrecords.block

import tech.feldman.betterrecords.api.wire.IRecordWire
import tech.feldman.betterrecords.block.tile.TileLaserCluster
import tech.feldman.betterrecords.client.render.RenderLaserCluster
import tech.feldman.betterrecords.helper.ConnectionHelper
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World

class BlockLaserCluster(name: String) : ModBlock(Material.WOOD, name), TESRProvider<TileLaserCluster>, ItemModelProvider {

    init {
        setHardness(4.8f)
        setResistance(4.8f)
    }

    override fun getTileEntityClass() = TileLaserCluster::class
    override fun getRenderClass() = RenderLaserCluster::class

    override fun getBoundingBox(state: IBlockState, source: IBlockAccess, pos: BlockPos) =
            AxisAlignedBB(0.12, 0.0, 0.12, 0.88, 0.76, 0.88)

    override fun onBlockAdded(world: World, pos: BlockPos, state: IBlockState) =
            world.notifyBlockUpdate(pos, state, state, 3)

    //override fun getLightValue(state: IBlockState, access: IBlockAccess, pos: BlockPos): Int TODO: Value from flash

    override fun removedByPlayer(state: IBlockState, world: World, pos: BlockPos, player: EntityPlayer, willHarvest: Boolean): Boolean {
        if (!world.isRemote) {
            (world.getTileEntity(pos) as? IRecordWire)?.let { te ->
                ConnectionHelper.clearConnections(world, te)
            }
        }
        return super.removedByPlayer(state, world, pos, player, willHarvest)
    }
}
