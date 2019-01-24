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
import tech.feldman.betterrecords.block.BlockSpeaker
import tech.feldman.betterrecords.block.tile.TileSpeaker
import tech.feldman.betterrecords.client.model.ModelLGSpeaker
import tech.feldman.betterrecords.client.model.ModelMDSpeaker
import tech.feldman.betterrecords.client.model.ModelSMSpeaker
import net.minecraft.client.renderer.GlStateManager.*
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer
import net.minecraft.util.ResourceLocation

class RenderSpeaker : TileEntitySpecialRenderer<TileSpeaker>() {

    val MODEL_SM = ModelSMSpeaker()
    val TEXTURE_SM = ResourceLocation(ID, "textures/models/smspeaker.png")

    val MODEL_MD = ModelMDSpeaker()
    val TEXTURE_MD = ResourceLocation(ID, "textures/models/mdspeaker.png")

    val MODEL_LG = ModelLGSpeaker()
    val TEXTURE_LG = ResourceLocation(ID, "textures/models/lgspeaker.png")

    override fun render(te: TileSpeaker?, x: Double, y: Double, z: Double, partialTicks: Float, destroyStage: Int, alpha: Float) {


        pushMatrix()

        translate(x + 0.5, y + 1.5, z + 0.5)
        rotate(180F, 0.0F, 0.0F, 1.0F)

        te?.let {
            rotate(te.rotation, 0F, 1F, 0F)
        }

        val size = when (te) {
            null -> BlockSpeaker.SpeakerSize.MEDIUM
            else -> te.size
        }

        bindTexture(when (size) {
            BlockSpeaker.SpeakerSize.SMALL -> TEXTURE_SM
            BlockSpeaker.SpeakerSize.MEDIUM -> TEXTURE_MD
            BlockSpeaker.SpeakerSize.LARGE -> TEXTURE_LG
        })

        when (size) {
            BlockSpeaker.SpeakerSize.SMALL -> MODEL_SM
            BlockSpeaker.SpeakerSize.MEDIUM -> MODEL_MD
            BlockSpeaker.SpeakerSize.LARGE -> MODEL_LG
        }.render(null, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F)

        popMatrix()
    }
}
