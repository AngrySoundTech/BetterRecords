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

import tech.feldman.betterrecords.ModConfig
import tech.feldman.betterrecords.api.connection.RecordConnection
import tech.feldman.betterrecords.api.wire.IRecordWire
import tech.feldman.betterrecords.api.wire.IRecordWireHome
import net.minecraft.util.ITickable
import java.util.*

abstract class SimpleRecordWireHome : ModTile(), IRecordWireHome, ITickable {

    var formTreble = ArrayList<Float>()
    var formBass = ArrayList<Float>()

    @Synchronized
    override fun addTreble(form: Float) {
        formTreble.add(form)
    }

    @Synchronized
    override fun addBass(form: Float) {
        formBass.add(form)
    }

    override var connections = mutableListOf<RecordConnection>()

    override var wireSystemInfo = HashMap<String, Int>()

    internal var playRadius = 0f

    override fun increaseAmount(wireComponent: IRecordWire) {
        val key = wireComponent.getName()
        val increase = if (wireSystemInfo.containsKey(key)) wireSystemInfo[key]!! + 1 else 1
        wireSystemInfo.put(key, increase)
        playRadius += wireComponent.songRadiusIncrease
    }

    override fun decreaseAmount(wireComponent: IRecordWire) {
        val key = wireComponent.getName()
        wireSystemInfo[key]?.let {
            wireSystemInfo.put(key, it - 1)
            if (it <= 0) {
                wireSystemInfo.remove(key)
            }
            playRadius -= wireComponent.songRadiusIncrease
        }
    }

    override fun update() {
        if (world.isRemote) {
            while (formTreble.size > 2500) {
                for (i in 0..24) {
                    formTreble.removeAt(0)
                    formBass.removeAt(0)
                }
            }
        }
    }

    abstract val songRadiusIncrease: Float

    override val songRadius: Float
        get() {
            val radius = songRadiusIncrease + playRadius
            val maxRadius = ModConfig.maxSpeakerRadius
            return if (radius <= maxRadius || maxRadius == -1) radius else maxRadius.toFloat()
        }

    override val tileEntity = this
}