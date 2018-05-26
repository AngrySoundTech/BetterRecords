package com.codingforcookies.betterrecords.util

import com.codingforcookies.betterrecords.api.wire.IRecordWireHome
import net.minecraft.client.Minecraft
import net.minecraft.util.SoundCategory
import net.minecraft.util.math.BlockPos

fun getVolumeForPlayerFromBlock(pos: BlockPos): Float {
    val player = Minecraft.getMinecraft().player
    val world = Minecraft.getMinecraft().world

    val te = world.getTileEntity(pos)

    (te as? IRecordWireHome)?.let {

        var dist = Math.abs(Math.sqrt(Math.pow(player.posX - pos.x, 2.toDouble()) +
                Math.pow(player.posY - pos.y, 2.toDouble()) +
                Math.pow(player.posZ - pos.z, 2.toDouble())))

        for (rc in te.connections) {
            val d = Math.abs(Math.sqrt(Math.pow(player.posX - rc.x2, 2.toDouble()) +
                    Math.pow(player.posY - rc.y2, 2.toDouble()) +
                    Math.pow(player.posZ - rc.z2, 2.toDouble())))
            if (d < dist) {
                dist = d
            }
        }

        return if (dist > te.songRadius + 10F) {
            -80F
        } else {
            val volume = dist * (50F / te.songRadius /
                    (Minecraft.getMinecraft().gameSettings.getSoundLevel(SoundCategory.MASTER) *
                            Minecraft.getMinecraft().gameSettings.getSoundLevel(SoundCategory.RECORDS)))

            if (volume > 80F) {
                -80F
            } else {
                0F - volume.toFloat()
            }
        }


    }

    return 0F
}
