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
import tech.feldman.betterrecords.block.tile.TileRecordEtcher
import tech.feldman.betterrecords.client.model.ModelRecordEtcher
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager.*
import net.minecraft.client.renderer.block.model.ItemCameraTransforms
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer
import net.minecraft.util.ResourceLocation

class RenderRecordEtcher : TileEntitySpecialRenderer<TileRecordEtcher>() {

    val MODEL = ModelRecordEtcher()
    val TEXTURE = ResourceLocation(ID, "textures/models/recordetcher.png")

    override fun render(te: TileRecordEtcher?, x: Double, y: Double, z: Double, partialTicks: Float, destroyStage: Int, alpha: Float) {

        te?.recordEntity?.let {
            pushMatrix()

            translate(x + 0.5F, y + .65F, z + 0.5F)
            rotate(90F, 1F, 0F, 0F)
            rotate(te.recordRotation * 57.3F, 0F, 0F, 1F)

            Minecraft.getMinecraft().renderItem.renderItem(it.item, ItemCameraTransforms.TransformType.NONE)

            popMatrix()
        }

        pushMatrix()
        translate(x + 0.5F, y + 1.5F, z + 0.5F)
        rotate(180F, 0F, 0F, 1F)
        bindTexture(TEXTURE)

        val needleLocation = te?.needleLocation ?: 0F
        val recordRotation = te?.recordRotation ?: 0F
        MODEL.render(null, needleLocation, recordRotation, 0F, 0F, 0F, 0.0625F)

        popMatrix()
    }
}
