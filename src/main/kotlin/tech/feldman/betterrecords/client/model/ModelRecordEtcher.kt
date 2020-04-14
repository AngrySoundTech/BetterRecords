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

class ModelRecordEtcher : ModelBase() {
    internal var blockCase: ModelRenderer
    internal var holder: ModelRenderer
    internal var frontLeftPeg: ModelRenderer
    internal var backRightPeg: ModelRenderer
    internal var backLeftPeg: ModelRenderer
    internal var frontRightPeg: ModelRenderer
    internal var top: ModelRenderer
    internal var topCase: ModelRenderer
    internal var etcher: ModelRenderer

    init {
        textureWidth = 64
        textureHeight = 64

        blockCase = ModelRenderer(this, 0, 0).apply {
            addBox(-7f, 0f, -7f, 14, 10, 14)
            setRotationPoint(0f, 14f, 0f)
            setTextureSize(64, 64)
            mirror = true
        }
        setRotation(blockCase, 0f, 0f, 0f)

        holder = ModelRenderer(this, 0, 4).apply {
            addBox(-0.5f, -1f, -0.5f, 1, 1, 1)
            setRotationPoint(0f, 14f, 0f)
            setTextureSize(64, 64)
            mirror = true
        }
        setRotation(holder, 0f, 0f, 0f)

        frontLeftPeg = ModelRenderer(this, 0, 0).apply {
            addBox(6f, -3f, -7f, 1, 3, 1)
            setRotationPoint(0f, 14f, 0f)
            setTextureSize(64, 64)
            mirror = false
        }
        setRotation(frontLeftPeg, 0f, 0f, 0f)

        backRightPeg = ModelRenderer(this, 0, 0).apply {
            addBox(-7f, -3f, 6f, 1, 3, 1)
            setRotationPoint(0f, 14f, 0f)
            setTextureSize(64, 64)
            mirror = true
        }
        setRotation(backRightPeg, 0f, 0f, 0f)

        backLeftPeg = ModelRenderer(this, 0, 0).apply {
            addBox(6f, -3f, 6f, 1, 3, 1)
            setRotationPoint(0f, 14f, 0f)
            setTextureSize(64, 64)
            mirror = false
        }
        setRotation(backLeftPeg, 0f, 0f, 0f)

        frontRightPeg = ModelRenderer(this, 0, 0).apply {
            addBox(-7f, -3f, -7f, 1, 3, 1)
            setRotationPoint(0f, 14f, 0f)
            setTextureSize(64, 64)
            mirror = true
        }
        setRotation(frontRightPeg, 0f, 0f, 0f)

        top = ModelRenderer(this, 0, 24).apply {
            addBox(-7f, -4f, -7f, 14, 1, 14)
            setRotationPoint(0f, 14f, 0f)
            setTextureSize(64, 64)
            mirror = true
        }
        setRotation(top, 0f, 0f, 0f)

        topCase = ModelRenderer(this, 0, 39).apply {
            addBox(-4f, -5f, -4f, 8, 1, 8)
            setRotationPoint(0f, 14f, 0f)
            setTextureSize(64, 64)
            mirror = true
        }
        setRotation(topCase, 0f, 0f, 0f)

        etcher = ModelRenderer(this, 0, 39).apply {
            addBox(-0.5f, -5f, -0.5f, 1, 3, 1)
            setRotationPoint(0f, 14f, 0f)
            setTextureSize(64, 64)
            mirror = true
        }
        setRotation(etcher, 0f, 0f, 0f)
    }

    override fun render(entity: Entity?, f: Float, f1: Float, f2: Float, f3: Float, f4: Float, f5: Float) {
        super.render(entity, f, f1, f2, f3, f4, f5)
        setRotationAngles(f, f1, f2, f3, f4, f5, entity)

        etcher.offsetX = f
        etcher.offsetY = if (f < .015f) f * 4 else .06f

        holder.rotateAngleY = 3f + f1

        blockCase.render(f5)
        holder.render(f5)
        frontLeftPeg.render(f5)
        backRightPeg.render(f5)
        backLeftPeg.render(f5)
        frontRightPeg.render(f5)
        top.render(f5)
        topCase.render(f5)
        etcher.render(f5)
    }

    private fun setRotation(model: ModelRenderer, x: Float, y: Float, z: Float) {
        model.rotateAngleX = x
        model.rotateAngleY = y
        model.rotateAngleZ = z
    }
}