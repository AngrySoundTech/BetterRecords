package com.hemogoblins.betterrecords.client.model;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import org.lwjgl.opengl.GL11;

public class ModelRecordPlayer extends Model {

    final ModelRenderer lid;
    final ModelRenderer body;
    final ModelRenderer holder;
    final ModelRenderer needle;
    final ModelRenderer needleStand;

    public ModelRecordPlayer() {
        super(RenderType::getEntityCutout);
        textureWidth = 64;
        textureHeight = 64;

        lid = new ModelRenderer(this, 0, 27);
        lid.addBox(-7f, -3f, -14f, 14, 3, 14);
        lid.setRotationPoint(0f, 12f, 7f);
        lid.setTextureSize(64, 64);
        lid.mirror = true;
        setRotation(lid, 0f, 0f, 0f);

        body = new ModelRenderer(this, 0, 0);
        body.addBox(0f, 0f, 0f, 15, 12, 15);
        body.setRotationPoint(-7.5f, 12f, -7.5f);
        body.setTextureSize(64, 64);
        body.mirror = true;
        setRotation(body, 0f, 0f, 0f);

        holder = new ModelRenderer(this, 0, 0);
        holder.addBox(-0.5f, 0f, -0.5f, 1, 1, 1);
        holder.setRotationPoint(0f, 11f, 0f);
        holder.setTextureSize(64, 64);
        holder.mirror = true;
        setRotation(holder, 0f, 0f, 0f);

        needle = new ModelRenderer(this, 0, 44);
        needle.addBox(-0.5f, -0.5f, -0.5f, 1, 1, 8);
        needle.setRotationPoint(5f, 11f, 5f);
        needle.setTextureSize(64, 64);
        needle.mirror = true;
        setRotation(needle, 0f, 3f, 0f);

        needleStand = new ModelRenderer(this, 0, 44);
        needleStand.addBox(-0.5f, -0.5f, -0.5f, 1, 1, 1);
        needleStand.setRotationPoint(5f, 12f, 5f);
        needleStand.setTextureSize(64, 64);
        needleStand.mirror = true;
        setRotation(needleStand, 0f, 0f, 0f);
    }

    private void setRotation(ModelRenderer model, Float x, Float y, Float z) {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }

    @Override
    public void render(MatrixStack ms, IVertexBuilder buffer, int light, int overlay, float red, float green, float blue, float alpha) {
        this.render(ms, buffer, light, overlay, red, green, blue, alpha, 0.0f, 0.0f, 0.0f, 0.0f);
    }

    // TODO: Make this play better a bit nicer with the renderer and what values are passed.
    public void render(MatrixStack ms, IVertexBuilder buffer, int light, int overlay, float red, float green, float blue, float alpha, float openAmount, float needleLocation, float recordRotation, float f) {
        lid.rotateAngleX = openAmount;
        needle.rotateAngleY = 3f + needleLocation;
        needle.rotateAngleX = recordRotation / 10 % 0.025f;
        holder.rotateAngleY = recordRotation;

        ms.push();
        GL11.glDisable(GL11.GL_CULL_FACE);
        lid.render(ms, buffer, light, overlay);
        GL11.glEnable(GL11.GL_CULL_FACE);
        ms.pop();

        body.render(ms, buffer, light, overlay);
        holder.render(ms, buffer, light, overlay);
        needle.render(ms, buffer, light, overlay);
        needleStand.render(ms, buffer, light, overlay);
    }
}
