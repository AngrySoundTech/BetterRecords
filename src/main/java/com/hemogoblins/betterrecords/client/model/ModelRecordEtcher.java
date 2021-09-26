package com.hemogoblins.betterrecords.client.model;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;

public class ModelRecordEtcher extends Model {

    final ModelRenderer blockCase;
    final ModelRenderer holder;
    final ModelRenderer frontLeftPeg;
    final ModelRenderer backRightPeg;
    final ModelRenderer backLeftPeg;
    final ModelRenderer frontRightPeg;
    final ModelRenderer top;
    final ModelRenderer topCase;
    final ModelRenderer etcher;

    public ModelRecordEtcher() {
        super(RenderType::getEntityCutout);
        textureWidth = 64;
        textureHeight = 64;

        blockCase = new ModelRenderer(this, 0, 0);
        blockCase.addBox(-7f, 0f, -7f, 14, 10, 14);
        blockCase.setRotationPoint(0f, 14f, 0f);
        blockCase.setTextureSize(64, 64);
        blockCase.mirror = true;
        setRotation(blockCase, 0f, 0f, 0f);

        holder = new ModelRenderer(this, 0, 4);
        holder.addBox(-0.5f, -1f, -0.5f, 1, 1, 1);
        holder.setRotationPoint(0f, 14f, 0f);
        holder.setTextureSize(64, 64);
        holder.mirror = true;
        setRotation(holder, 0f, 0f, 0f);

        frontLeftPeg = new ModelRenderer(this, 0, 0);
        frontLeftPeg.addBox(6f, -3f, -7f, 1, 3, 1);
        frontLeftPeg.setRotationPoint(0f, 14f, 0f);
        frontLeftPeg.setTextureSize(64, 64);
        frontLeftPeg.mirror = false;
        setRotation(frontLeftPeg, 0f, 0f, 0f);

        backRightPeg = new ModelRenderer(this, 0, 0);
        backRightPeg.addBox(-7f, -3f, 6f, 1, 3, 1);
        backRightPeg.setRotationPoint(0f, 14f, 0f);
        backRightPeg.setTextureSize(64, 64);
        backRightPeg.mirror = true;
        setRotation(backRightPeg, 0f, 0f, 0f);

        backLeftPeg = new ModelRenderer(this, 0, 0);
        backLeftPeg.addBox(6f, -3f, 6f, 1, 3, 1);
        backLeftPeg.setRotationPoint(0f, 14f, 0f);
        backLeftPeg.setTextureSize(64, 64);
        backLeftPeg.mirror = false;
        setRotation(backLeftPeg, 0f, 0f, 0f);

        frontRightPeg = new ModelRenderer(this, 0, 0);
        frontRightPeg.addBox(-7f, -3f, -7f, 1, 3, 1);
        frontRightPeg.setRotationPoint(0f, 14f, 0f);
        frontRightPeg.setTextureSize(64, 64);
        frontRightPeg.mirror = true;
        setRotation(frontRightPeg, 0f, 0f, 0f);

        top = new ModelRenderer(this, 0, 24);
        top.addBox(-7f, -4f, -7f, 14, 1, 14);
        top.setRotationPoint(0f, 14f, 0f);
        top.setTextureSize(64, 64);
        top.mirror = true;
        setRotation(top, 0f, 0f, 0f);

        topCase = new ModelRenderer(this, 0, 39);
        topCase.addBox(-4f, -5f, -4f, 8, 1, 8);
        topCase.setRotationPoint(0f, 14f, 0f);
        topCase.setTextureSize(64, 64);
        topCase.mirror = true;
        setRotation(topCase, 0f, 0f, 0f);

        etcher = new ModelRenderer(this, 0, 39);
        etcher.addBox(-0.5f, -5f, -0.5f, 1, 3, 1);
        etcher.setRotationPoint(0f, 14f, 0f);
        etcher.setTextureSize(64, 64);
        etcher.mirror = true;
        setRotation(etcher, 0f, 0f, 0f);
    }

    private void setRotation(ModelRenderer model, Float x, Float y, Float z) {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }

    @Override
    public void render(MatrixStack ms, IVertexBuilder buffer, int light, int overlay, float red, float green, float blue, float alpha) {
        this.render(ms, buffer, light, overlay, red, green, blue, alpha, 0.0f, 0.0f);
    }

    public void render(MatrixStack ms, IVertexBuilder buffer, int light, int overlay, float red, float green, float blue, float alpha, float needleLocation, float recordLocation) {

        // etcher.offsetX = f
        // etcher.offsetY = if (f < .015f) f * 4 else .06f
        holder.rotateAngleY = 3f + recordLocation;

        blockCase.render(ms, buffer, light, overlay, red, green, blue, alpha);
        holder.render(ms, buffer, light, overlay, red, green, blue, alpha);
        frontLeftPeg.render(ms, buffer, light, overlay, red, green, blue, alpha);
        backRightPeg.render(ms, buffer, light, overlay, red, green, blue, alpha);
        backLeftPeg.render(ms, buffer, light, overlay, red, green, blue, alpha);
        frontRightPeg.render(ms, buffer, light, overlay, red, green, blue, alpha);
        top.render(ms, buffer, light, overlay, red, green, blue, alpha);
        topCase.render(ms, buffer, light, overlay, red, green, blue, alpha);
        etcher.render(ms, buffer, light, overlay, red, green, blue, alpha);
    }
}
