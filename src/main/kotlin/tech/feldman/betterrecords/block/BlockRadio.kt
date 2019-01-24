package tech.feldman.betterrecords.block

import tech.feldman.betterrecords.api.sound.ISoundHolder
import tech.feldman.betterrecords.api.wire.IRecordWire
import tech.feldman.betterrecords.api.wire.IRecordWireManipulator
import tech.feldman.betterrecords.block.tile.TileRadio
import tech.feldman.betterrecords.client.render.RenderRadio
import tech.feldman.betterrecords.helper.ConnectionHelper
import tech.feldman.betterrecords.helper.nbt.getSounds
import tech.feldman.betterrecords.item.ModItems
import tech.feldman.betterrecords.network.PacketHandler
import tech.feldman.betterrecords.network.PacketRadioPlay
import tech.feldman.betterrecords.network.PacketSoundStop
import net.minecraft.block.Block
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.item.EntityItem
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.util.*
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World
import java.util.*

class BlockRadio(name: String) : ModBlockDirectional(Material.WOOD, name), TESRProvider<TileRadio>, ItemModelProvider {

    init {
        setHardness(2f)
        setResistance(6.3f)
    }

    override fun getTileEntityClass() = TileRadio::class
    override fun getRenderClass() = RenderRadio::class

    override fun onBlockAdded(world: World, pos: BlockPos, state: IBlockState) =
            world.notifyBlockUpdate(pos, state, state, 3)

    override fun getBoundingBox(state: IBlockState, block: IBlockAccess, pos: BlockPos) = when (getMetaFromState(state)) {
        0, 2 -> AxisAlignedBB(0.13, 0.0, 0.2, 0.87, 0.98, 0.8)
        1, 3 -> AxisAlignedBB(0.2, 0.0, 0.13, 0.8, 0.98, 0.87)
        else -> Block.FULL_BLOCK_AABB
    }

    override fun onBlockActivated(world: World, pos: BlockPos, state: IBlockState, player: EntityPlayer, hand: EnumHand, side: EnumFacing, hitX: Float, hitY: Float, hitZ: Float): Boolean {
        if (player.heldItemMainhand.item is IRecordWireManipulator) return false

        (world.getTileEntity(pos) as? TileRadio)?.let { te ->
            if (player.isSneaking) {
                te.opening = !te.opening
                world.notifyBlockUpdate(pos, state, state, 3)
                world.playSound(pos.x.toDouble(), pos.y.toDouble() + 0.5, pos.z.toDouble(), SoundEvent.REGISTRY.getObject(ResourceLocation("block.chest.close")), SoundCategory.NEUTRAL, 0.2f, world.rand.nextFloat() * 0.2f + 3f, false)
            } else if (te.opening) {
                if (!te.crystal.isEmpty) {
                    if (!world.isRemote) dropItem(world, pos)
                    te.crystal = ItemStack.EMPTY
                    world.notifyBlockUpdate(pos, state, state, 3)
                } else if (player.heldItemMainhand.item == ModItems.itemFrequencyCrystal && getSounds(player.heldItemMainhand).isNotEmpty()) {
                    te.crystal = player.heldItemMainhand
                    world.notifyBlockUpdate(pos, state, state, 3)
                    player.heldItemMainhand.count--
                    if (!world.isRemote) {
                        PacketHandler.sendToAll(PacketRadioPlay(
                                pos,
                                world.provider.dimension,
                                te.songRadius,
                                getSounds(te.crystal).first().name,
                                getSounds(te.crystal).first().url
                        ))
                    }
                }
            }
            return true
        }
        return false
    }

    override fun removedByPlayer(state: IBlockState, world: World, pos: BlockPos, player: EntityPlayer, willHarvest: Boolean): Boolean {
        if (!world.isRemote) {
            (world.getTileEntity(pos) as? IRecordWire)?.let { te ->
                ConnectionHelper.clearConnections(world, te)
            }
        }
        return super.removedByPlayer(state, world, pos, player, willHarvest)
    }

    override fun breakBlock(world: World, pos: net.minecraft.util.math.BlockPos, state: IBlockState) {
        dropItem(world, pos)
        super.breakBlock(world, pos, state)
    }

    private fun dropItem(world: World, pos: BlockPos) {
        (world.getTileEntity(pos) as? TileRadio)?.let { te ->
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
                PacketHandler.sendToAll(PacketSoundStop(te.pos, world.provider.dimension))
            }
        }
    }
}
