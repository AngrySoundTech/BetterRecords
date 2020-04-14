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
import tech.feldman.betterrecords.block.tile.TileRecordPlayer
import tech.feldman.betterrecords.client.model.ModelRecordPlayer
import tech.feldman.betterrecords.client.render.helper.renderConnectionsAndInfo
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager.*
import net.minecraft.client.renderer.block.model.ItemCameraTransforms
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer
import net.minecraft.util.ResourceLocation

class RenderRecordPlayer : TileEntitySpecialRenderer<TileRecordPlayer>() {

    val MODEL = ModelRecordPlayer()
    val TEXTURE = ResourceLocation(ID, "textures/models/recordplayer.png")

    override fun render(te: TileRecordPlayer?, x: Double, y: Double, z: Double, scale: Float, destroyStage: Int, alpha: Float) {

        //region RENDER_BLOCK
        pushMatrix()

        translate(x.toFloat() + 0.5f, y.toFloat() + 1.5f, z.toFloat() + 0.5f)
        rotate(180f, 0.0f, 0.0f, 1.0f)

        val metadata = te?.blockMetadata ?: 0
        rotate(metadata * 90 + 180F, 0.0f, 1.0f, 0.0f)

        bindTexture(TEXTURE)

        val openAmount = te?.openAmount ?: 0F
        val needleLocation = te?.needleLocation ?: 0F
        val recordRotation = te?.recordRotation ?: 0F
        MODEL.render(null, openAmount, needleLocation, recordRotation, 0.0f, 0.0f, 0.0625f)

        popMatrix()
        //endregion RENDER_BLOCK

        te?.let {
            //region RENDER_RECORD
            te.recordEntity?.let {
                pushMatrix()

                translate(x.toFloat() + .5f, y.toFloat() + .76f, z.toFloat() + .5f)
                rotate(90f, 1f, 0f, 0f)
                rotate(te.recordRotation * 57.3f, 0f, 0f, 1f)
                disableLighting()
                disableCull()
                Minecraft.getMinecraft().renderItem.renderItem(te.recordEntity?.item, ItemCameraTransforms.TransformType.NONE)
                enableLighting()
                enableCull()
                color(1f, 1f, 1f)

                popMatrix()
            }
            //endregion RENDER_RECORD

            renderConnectionsAndInfo(te, te.pos, x, y, z)
        }
    }
}
