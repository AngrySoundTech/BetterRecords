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
package tech.feldman.betterrecords.client.render

import tech.feldman.betterrecords.ID
import tech.feldman.betterrecords.ModConfig
import tech.feldman.betterrecords.block.tile.TileLaserCluster
import tech.feldman.betterrecords.client.model.ModelLaserCluster
import net.minecraft.client.renderer.GlStateManager.*
import net.minecraft.client.renderer.RenderHelper
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer
import net.minecraft.util.ResourceLocation
import org.lwjgl.opengl.GL11

class RenderLaserCluster : TileEntitySpecialRenderer<TileLaserCluster>() {

    val MODEL = ModelLaserCluster()
    val TEXTURE = ResourceLocation(ID, "textures/models/lasercluster.png")

    override fun render(te: TileLaserCluster?, x: Double, y: Double, z: Double, scale: Float, unknown: Int, alpha: Float) {

        pushMatrix()

        translate(x.toFloat() + 0.5f, y.toFloat() + 1.5f, z.toFloat() + 0.5f)
        rotate(180f, 0.0f, 0.0f, 1.0f)

        bindTexture(TEXTURE)

        MODEL.render(null, 0f, 0f, 0f, 0.0f, 0.0f, 0.0625f)

        MODEL.renderEmitor(null, 0f, 0f, 0f, 0.0f, 0.0f, 0.0625f)

        te?.let {

            if (te.r != 0.0f && te.g != 0.0f && te.b != 0.0f) {
                disableTexture2D()
                enableBlend()
                color(te.r, te.g, te.b, if (ModConfig.client.flashMode == 1) .2f else .4f)
            }

            //MODEL.renderEmitter(null, 0f, 0f, 0f, 0.0f, 0.0f, 0.0625f)
            if (te.r != 0.0f && te.g != 0.0f && te.b != 0.0f) {
                disableBlend()
                enableTexture2D()
            }

            color(1f, 1f, 1f, 1f)
            enableLighting()

            translate(0.0f, 1.0f, 0.0f)

            if (te.bass != 0F && ModConfig.client.flashMode > 0) {
                pushMatrix()

                disableLighting()
                disableTexture2D()
                enableBlend()
                RenderHelper.disableStandardItemLighting()

                glLineWidth(te.bass / 2)

                var pitch = 0f
                while (pitch < 9f) {
                    rotate(200f / 3, 0f, 1f, 0f)
                    var yaw = 0f
                    while (yaw < 18f) {
                        rotate(200f / 9, 0f, 0f, 1f)
                        glBegin(GL11.GL_LINE_STRIP)
                        run {
                            color(te.r, te.g, te.b, if (ModConfig.client.flashMode == 1) .2f else .4f)
                            GL11.glVertex2f(0f, 0f)

                            val xx = Math.cos(pitch * (Math.PI / 180)).toFloat() * 20f
                            val yy = Math.sin(yaw * (Math.PI / 180)).toFloat() * 20f

                            GL11.glVertex2f(xx, yy)
                        }
                        glEnd()
                        yaw += 1f
                    }
                    pitch += 1f
                }

                glLineWidth(1F)

                RenderHelper.enableStandardItemLighting()
                disableBlend()
                enableTexture2D()
                enableLighting()

                popMatrix()

                color(1f, 1f, 1f, 1f)
            }
        }

        popMatrix()
    }
}
