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
import tech.feldman.betterrecords.block.tile.TileRadio
import tech.feldman.betterrecords.client.model.ModelRadio
import tech.feldman.betterrecords.client.render.helper.renderConnectionsAndInfo
import net.minecraft.client.renderer.GlStateManager.*
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer
import net.minecraft.item.ItemStack
import net.minecraft.util.ResourceLocation

class RenderRadio : TileEntitySpecialRenderer<TileRadio>() {

    val MODEL = ModelRadio()
    val TEXTURE = ResourceLocation(ID, "textures/models/radio.png")

    override fun render(te: TileRadio?, x: Double, y: Double, z: Double, partialTicks: Float, destroyStage: Int, alpha: Float) {
        pushMatrix()

        translate(x + 0.5, y + 1.5, z + 0.5)
        rotate(180F, 0.0F, 0.0F, 1.0F)

        te?.let {
            rotate(te.blockMetadata * 90 + 180F, 0.0F, 1.0F, 0.0F)
        }

        bindTexture(TEXTURE)

        val openAmount = te?.openAmount ?: 0F
        val crystalFloat = te?.crystalFloaty ?: 0F
        val crystal = te?.crystal ?: ItemStack.EMPTY
        MODEL.render(null, openAmount, crystalFloat, 0F, 0F, 0F, 0.0625F, crystal)

        popMatrix()

        te?.let {
            renderConnectionsAndInfo(te, te.pos, x, y, z)
        }
    }
}
