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
package tech.feldman.betterrecords.client.model

import net.minecraft.client.model.ModelBase
import net.minecraft.client.model.ModelRenderer
import net.minecraft.entity.Entity

class ModelLaserCluster : ModelBase() {
    internal var Base: ModelRenderer
    internal var Emitor: ModelRenderer
    internal var Case: ModelRenderer

    init {
        textureWidth = 64
        textureHeight = 64

        this.Emitor = ModelRenderer(this, 0, 22).apply {
            setRotationPoint(-3.0F, 13.0F, -3.0F)
            addBox(0.0F, 0.0F, 0.0F, 6, 8, 6)
        }
        this.Base = ModelRenderer(this, 0, 0).apply {
            setRotationPoint(0.0F, 21.0F, 0.0F)
            addBox(-6.0F, 0.0F, -6.0F, 12, 3, 12)
        }
        this.Case = ModelRenderer(this, 0, 43).apply {
            setRotationPoint(0.0F, 21.0F, 0.0F)
            addBox(-6.0F, -9.0F, -6.0F, 12, 9, 12)
        }
    }

    override fun render(entity: Entity?, f: Float, f1: Float, f2: Float, f3: Float, f4: Float, f5: Float) {
        super.render(entity, f, f1, f2, f3, f4, f5)
        setRotationAngles(f, f1, f2, f3, f4, f5, entity)
        Base.render(f5)
        Case.render(f5)
    }

    fun renderEmitor(entity: Entity?, f: Float, f1: Float, f2: Float, f3: Float, f4: Float, f5: Float) {
        super.render(entity, f, f1, f2, f3, f4, f5)
        setRotationAngles(f, f1, f2, f3, f4, f5, entity)
        Emitor.render(f5)
    }

    private fun setRotationAngles(model: ModelRenderer, x: Float, y: Float, z: Float) {
        model.rotateAngleX = x
        model.rotateAngleY = y
        model.rotateAngleZ = z
    }
}
