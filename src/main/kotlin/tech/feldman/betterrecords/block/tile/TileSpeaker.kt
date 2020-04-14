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
package tech.feldman.betterrecords.block.tile

import tech.feldman.betterrecords.api.connection.RecordConnection
import tech.feldman.betterrecords.api.wire.IRecordWire
import tech.feldman.betterrecords.block.BlockSpeaker
import tech.feldman.betterrecords.extensions.set
import tech.feldman.betterrecords.helper.ConnectionHelper
import net.minecraft.nbt.NBTTagCompound

class TileSpeaker : ModTile(), IRecordWire {

    var rotation = 0f

    var size = BlockSpeaker.SpeakerSize.SMALL
        get() {
            // Horrible hacky solution to fix an issue that only seems to occur sometimes.
            // This will get properly fixed in the next major refactoring pass of blocks / tiles.
            // Which is planned very shortly. In order to properly fix the issue, the way I do many things should be refactored
            return try {
                world?.getBlockState(pos)?.getValue(BlockSpeaker.PROPERTYSIZE) ?: BlockSpeaker.SpeakerSize.SMALL
            } catch (e: Exception) {
                BlockSpeaker.SpeakerSize.SMALL
            }
        }

    override var connections = mutableListOf<RecordConnection>()

    override fun getName() = when (size) {
        BlockSpeaker.SpeakerSize.SMALL -> "Small"
        BlockSpeaker.SpeakerSize.MEDIUM -> "Medium"
        BlockSpeaker.SpeakerSize.LARGE -> "Large"
    } + " Speaker"

    override val songRadiusIncrease
        get() = when (size) {
            BlockSpeaker.SpeakerSize.SMALL -> 15F
            BlockSpeaker.SpeakerSize.MEDIUM -> 35F
            BlockSpeaker.SpeakerSize.LARGE -> 70F
        }

    override fun readFromNBT(compound: NBTTagCompound) = compound.run {
        super.readFromNBT(compound)

        rotation = getFloat("rotation")
        connections = ConnectionHelper.unserializeConnections(getString("connections")).toMutableList()
    }

    override fun writeToNBT(compound: NBTTagCompound) = compound.apply {
        super.writeToNBT(compound)

        set("rotation", rotation)
        set("connections", ConnectionHelper.serializeConnections(connections))
    }
}
