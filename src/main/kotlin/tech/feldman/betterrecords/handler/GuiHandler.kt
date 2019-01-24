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
package tech.feldman.betterrecords.handler

import tech.feldman.betterrecords.block.tile.TileFrequencyTuner
import tech.feldman.betterrecords.block.tile.TileRecordEtcher
import tech.feldman.betterrecords.client.gui.ContainerFrequencyTuner
import tech.feldman.betterrecords.client.gui.ContainerRecordEtcher
import tech.feldman.betterrecords.client.gui.GuiFrequencyTuner
import tech.feldman.betterrecords.client.gui.GuiRecordEtcher
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.fml.common.network.IGuiHandler

class GuiHandler : IGuiHandler {

    override fun getServerGuiElement(id: Int, player: EntityPlayer, world: World, x: Int, y: Int, z: Int) = when (id) {
        0 -> ContainerRecordEtcher(player.inventory, world.getTileEntity(BlockPos(x, y, z)) as TileRecordEtcher)
        1 -> ContainerFrequencyTuner(player.inventory, world.getTileEntity(BlockPos(x, y, z)) as TileFrequencyTuner)
        else -> null
    }

    override fun getClientGuiElement(id: Int, player: EntityPlayer, world: World, x: Int, y: Int, z: Int) = when (id) {
        0 -> GuiRecordEtcher(player.inventory, world.getTileEntity(BlockPos(x, y, z)) as TileRecordEtcher)
        1 -> GuiFrequencyTuner(player.inventory, world.getTileEntity(BlockPos(x, y, z)) as TileFrequencyTuner)
        else -> null
    }
}
