package com.hemogoblins.betterrecords.block.renderer

import com.hemogoblins.betterrecords.BetterRecords
import com.hemogoblins.betterrecords.block.entity.RecordEtcherBlockEntity
import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import com.mojang.math.Axis
import net.minecraft.client.model.geom.ModelLayerLocation
import net.minecraft.client.model.geom.ModelPart
import net.minecraft.client.model.geom.PartPose
import net.minecraft.client.model.geom.builders.*
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider
import net.minecraft.client.renderer.entity.ItemRenderer
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.Mth
import net.minecraft.world.item.ItemDisplayContext
import net.minecraft.world.item.ItemStack

class RecordEtcherRenderer: BlockEntityRenderer<RecordEtcherBlockEntity> {

    companion object {
        val MODEL_LAYER_LOCATION = ModelLayerLocation(ResourceLocation(BetterRecords.ID, "record_etcher_model"), "main")

        fun createBodyLayer(): LayerDefinition {
            return LayerDefinition.create(MeshDefinition().apply {
                root.addOrReplaceChild("body", CubeListBuilder.create()
                        .texOffs(0, 39).mirror().addBox(-8.5f, -15.0f, 7.5f, 1.0f, 3.0f, 1.0f, CubeDeformation(0.0f)).mirror(false),
                        PartPose.offset(8.0f, 8.0f, -8.0f) // Units are in 16ths of a block. This centers the model on pose origin.
                )
            }, 64, 64)
        }

    }

    private val itemRenderer: ItemRenderer
    private val model: ModelPart

    constructor(context: BlockEntityRendererProvider.Context) {
        this.itemRenderer = context.itemRenderer
        this.model = context.bakeLayer(MODEL_LAYER_LOCATION).getChild("body")
    }

    override fun render(
            etcher: RecordEtcherBlockEntity,
            partialTick: Float,
            poseStack: PoseStack,
            bufferSource: MultiBufferSource,
            packedLight: Int,
            packedOverlay: Int
    ) {
        val time = (etcher.level?.gameTime ?: 0) + partialTick

        etcher.getSlottedRecord().takeIf { !it.isEmpty }?.let {
            renderRecord(it, (time * 3f) % 360, etcher, poseStack, bufferSource, packedLight, packedOverlay)
        }

        this.renderNeedle(time, poseStack, bufferSource, packedLight, packedOverlay)

        // todo: rotate peg with record
        // TODO: trigger anim from TE state
    }

    private fun renderNeedle(time: Float, poseStack: PoseStack, bufferSource: MultiBufferSource, packedLight: Int, packedOverlay: Int) {
        val consumer: VertexConsumer = bufferSource.getBuffer(RenderType.entityCutout(ResourceLocation(BetterRecords.ID, "textures/block/record_etcher.png")))

        var shift = time * .002f
        shift = Math.abs((shift % 2) - 1) // Ping-pong 0..1

        poseStack.pushPose()
        poseStack.apply {
            translate(0.5f, 0.5f, 0.5f) // Move to center of block
            mulPose(Axis.XP.rotationDegrees(180f))
            translate(0f, 0.05f, Mth.lerp(shift, 0.14f, .21f))

        }

        this.model.render(poseStack, consumer, packedLight, packedOverlay)


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