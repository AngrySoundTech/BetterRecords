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
package tech.feldman.betterrecords.client.render.helper

import tech.feldman.betterrecords.api.wire.IRecordWireHome
import tech.feldman.betterrecords.api.wire.IRecordWireManipulator
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.util.math.BlockPos
import org.lwjgl.opengl.GL11

fun renderConnectionsAndInfo(te: IRecordWireHome, pos: BlockPos, x: Double, y: Double, z: Double) {
    (Minecraft.getMinecraft().player.heldItemMainhand.item as? IRecordWireManipulator)?.let {
        GlStateManager.pushMatrix()

        GlStateManager.translate(x + 0.5, y + 0.5, z + 0.5)

        //region RENDER_CONNECTIONS
        if (te.connections.size > 0) {
            GlStateManager.color(0F, 0F, 0F)
            GlStateManager.disableTexture2D()

            GlStateManager.glLineWidth(2F)

            for (rec in te.connections) {
                val x1 = -(pos.x - rec.x2).toFloat()
                val y1 = -(pos.y - rec.y2).toFloat()
                val z1 = -(pos.z - rec.z2).toFloat()

                GlStateManager.pushMatrix()

                GlStateManager.glBegin(GL11.GL_LINE_STRIP)
                GlStateManager.glVertex3f(0F, 0F, 0F)
                GlStateManager.glVertex3f(x1, y1, z1)
                GlStateManager.glEnd()

                GlStateManager.popMatrix()
            }

            GlStateManager.enableTexture2D()
            GlStateManager.color(1F, 1F, 1F)
        }
        //endregion RENDER_CONNECTIONS

        GlStateManager.scale(0.01F, -0.01F, 0.01F)
        GlStateManager.rotate(-Minecraft.getMinecraft().renderManager.playerViewY - 180F, 0F, 1F, 0F)

        GlStateManager.color(1F, 1F, 1F)
        var currentY = te.wireSystemInfo.size * -10 - 75
        val fontRenderer = Minecraft.getMinecraft().fontRenderer
        val radiusString = "Play Radius: ${te.songRadius}"
        fontRenderer.drawString(radiusString, -fontRenderer.getStringWidth(radiusString) / 2, currentY, 0xFFFFFF)
        for (info in te.wireSystemInfo.entries) {
            currentY += 10
            val infoString = "${info.value}x ${info.key}"
            fontRenderer.drawString(infoString, -fontRenderer.getStringWidth(infoString) / 2, currentY, 0xFFFFFF)
        }

        GlStateManager.popMatrix()
    }
}
