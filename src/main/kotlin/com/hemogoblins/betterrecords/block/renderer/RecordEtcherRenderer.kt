package com.hemogoblins.betterrecords.block.renderer

import com.hemogoblins.betterrecords.BetterRecords
import com.hemogoblins.betterrecords.block.entity.RecordEtcherBlockEntity
import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import com.mojang.math.Axis
import net.minecraft.client.model.geom.ModelLayerLocation
import net.minecraft.client.model.geom.ModelPart
import net.minecraft.client.model.geom.PartPose
import net.minecraft.client.model.geom.builders.CubeDeformation
import net.minecraft.client.model.geom.builders.CubeListBuilder
import net.minecraft.client.model.geom.builders.LayerDefinition
import net.minecraft.client.model.geom.builders.MeshDefinition
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider
import net.minecraft.client.renderer.entity.ItemRenderer
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.Mth
import net.minecraft.world.item.ItemDisplayContext
import net.minecraft.world.item.ItemStack
import kotlin.math.abs

class RecordEtcherRenderer(context: BlockEntityRendererProvider.Context): BlockEntityRenderer<RecordEtcherBlockEntity> {

    companion object {
        val MODEL_LAYER_LOCATION = ModelLayerLocation(ResourceLocation(BetterRecords.ID, "record_etcher_model"), "main")

        fun createBodyLayer(): LayerDefinition {
            return LayerDefinition.create(MeshDefinition().apply {
                val offset = PartPose.offset(8.0f, 8.0f, -8.0f) // Units are in 16ths of a block. This centers the model on pose origin.
                root.addOrReplaceChild("needle", CubeListBuilder.create().texOffs(0, 39).mirror().addBox(-8.5f, -15.0f, 7.5f, 1.0f, 3.0f, 1.0f, CubeDeformation(0.0f)).mirror(false), offset)
                root.addOrReplaceChild("peg", CubeListBuilder.create()	.texOffs(0, 4).mirror().addBox(-8.5F, -11.0F, 7.5F, 1.0F, 1.0F, 1.0F, CubeDeformation(0.0F)).mirror(false), offset)
            }, 64, 64)
        }
    }

    private val itemRenderer: ItemRenderer = context.itemRenderer
    private val model_needle: ModelPart
    private val model_peg: ModelPart

    init {
        context.bakeLayer(MODEL_LAYER_LOCATION).also {
            model_needle = it.getChild("needle")
            model_peg = it.getChild("peg")
        }
    }

    // todo: look into profiling and making this logic more efficient
    override fun render(
            entity: RecordEtcherBlockEntity,
            partialTick: Float,
            poseStack: PoseStack,
            bufferSource: MultiBufferSource,
            packedLight: Int,
            packedOverlay: Int
    ) {
        val time = (entity.level?.gameTime ?: 0) + partialTick
        val consumer: VertexConsumer = bufferSource.getBuffer(RenderType.entityCutout(ResourceLocation(BetterRecords.ID, "textures/block/record_etcher.png")))
        val isEtching = (!entity.isEmpty) // && isValid

        val rot = if (isEtching) (time * 3f) % 360 else 0f

        poseStack.pushPose()
        poseStack.apply {
            translate(0.5f, 0.5f, 0.5f) // Move to center of block
            mulPose(Axis.XP.rotationDegrees(180f))
        }

        this.renderNeedle(time, entity.getNeedleOpenness(partialTick), poseStack, consumer, packedLight, packedOverlay)

        poseStack.pushPose()
        poseStack.mulPose(Axis.YP.rotationDegrees(rot))
        model_peg.render(poseStack, consumer, packedLight, packedOverlay)
        poseStack.popPose()

        poseStack.popPose()

        entity.getSlottedItem().takeIf { !it.isEmpty }?.let {
            renderRecord(it, rot, entity, poseStack, bufferSource, packedLight, packedOverlay)
        }
    }

    private fun renderNeedle(time: Float, openness: Float, poseStack: PoseStack, consumer: VertexConsumer, packedLight: Int, packedOverlay: Int) {
        var f1 = openness
        f1 = 1.0f - f1
        f1 = 1.0f - f1 * f1 * f1

        val shift = abs(((time * .004f) % 2) - 1) // Ping-pong 0..1

        poseStack.pushPose()
        poseStack.translate(
                0f,
                mapRange(0f, 1f, 0f,0.05f, easeInOutExpo(openness)),
                mapRange(0f, 1f, 0f, Mth.lerp(shift, 0.14f, .21f), f1)
        )

        model_needle.render(poseStack, consumer, packedLight, packedOverlay)
        poseStack.popPose()
    }

    private fun renderRecord(
            itemStack: ItemStack,
            rotation: Float,
            etcher: RecordEtcherBlockEntity,
            poseStack: PoseStack,
            bufferSource: MultiBufferSource,
            packedLight: Int,
            packedOverlay: Int
    ) {
        poseStack.pushPose()
        poseStack.apply {
            translate(0.5f, 0.65f, 0.5f)
            mulPose(Axis.XP.rotationDegrees(90f))
            mulPose(Axis.ZP.rotationDegrees(rotation))
        }

        itemRenderer.renderStatic(itemStack, ItemDisplayContext.FIXED, packedLight,
                packedOverlay, poseStack, bufferSource, etcher.level, 1)

        poseStack.popPose()
    }
}

private fun mapRange(fromMin: Float, fromMax: Float, toMin: Float, toMax: Float, value: Float): Float {
    return (value-fromMin) / (fromMax-fromMin) * (toMax-toMin) + toMin
}
private fun easeInOutExpo(fac: Float): Float {
    if (fac == 0f) return 0f
    else if (fac == 1f) return 1f
    else if (fac < 0.5f) return (Math.pow(2.0, 20 * fac.toDouble() - 10) / 2).toFloat()
    else return ((2 - Math.pow(2.0, -20 * fac.toDouble() + 10)) / 2).toFloat()
}