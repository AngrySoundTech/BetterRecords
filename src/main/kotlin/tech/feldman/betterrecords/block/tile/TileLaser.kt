package tech.feldman.betterrecords.block.tile

import tech.feldman.betterrecords.api.connection.RecordConnection
import tech.feldman.betterrecords.api.record.IRecordAmplitude
import tech.feldman.betterrecords.api.wire.IRecordWire
import tech.feldman.betterrecords.helper.ConnectionHelper
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.ITickable
import java.util.*

class TileLaser : ModTile(), IRecordWire, IRecordAmplitude, ITickable {

    override var connections = mutableListOf<RecordConnection>()

    var pitch = 0F
    var yaw = 0F

    var length = 10

    var r = 0F
    var g = 0F
    var b = 0F

    override var treble = 0F
    override var bass = 0F
        set(value) {
            field = value

            Random(value.toLong() + System.nanoTime()).run {
                r = nextFloat()
                g = nextFloat()
                b = nextFloat()
            }

            Random().nextInt().also {
                r += if (it == 0) .3f else -.1f
                g += if (it == 1) .3f else -.1f
                b += if (it == 2) .3f else -.1f
            }

            if (r < .2F) r += r
            if (g < .2F) g += g
            if (b < .2F) b += b
        }

    override fun getName() = "Laser"

    override val songRadiusIncrease = 0F

    override fun update() {
        if (bass > 0) bass--
        if (bass < 0) bass = 0F
    }

    override fun readFromNBT(compound: NBTTagCompound) = compound.run {
        super.readFromNBT(compound)

        connections = ConnectionHelper.unserializeConnections(getString("connections")).toMutableList()
        pitch = getFloat("pitch")
        yaw = getFloat("yaw")
        length = getInteger("length")
    }

    override fun writeToNBT(compound: NBTTagCompound) = compound.apply {
        super.writeToNBT(compound)

        setString("connections", ConnectionHelper.serializeConnections(connections))
        setFloat("pitch", pitch)
        setFloat("yaw", yaw)
        setInteger("length", length)
    }
}