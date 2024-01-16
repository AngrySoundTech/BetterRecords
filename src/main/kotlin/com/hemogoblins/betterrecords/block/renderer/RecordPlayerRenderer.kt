package com.hemogoblins.betterrecords.block.renderer

import com.hemogoblins.betterrecords.BetterRecords
import com.hemogoblins.betterrecords.block.RecordPlayerBlock.Companion.FACING
import com.hemogoblins.betterrecords.block.entity.RecordPlayerBlockEntity
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
import net.minecraft.world.item.ItemDisplayContext
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level

class RecordPlayerRenderer: BlockEntityRenderer<RecordPlayerBlockEntity> {

    companion object {
        val MODEL_LAYER_LOCATION = ModelLayerLocation(ResourceLocation(BetterRecords.ID, "record_player_model"), "main")

        fun createBodyLayer(): LayerDefinition {
            val offset = PartPose.ZERO // PartPose.offset(0.0f, 0.0f, 0.0f)
            val deformation = CubeDeformation.NONE //CubeDeformation(0.0f)

            return LayerDefinition.create(MeshDefinition().apply {
                root.addOrReplaceChild("base", CubeListBuilder.create()
                        .texOffs(0, 0).addBox(-7.5f, -12.0f, -7.5f, 15.0f, 12.0f, 15.0f, deformation) // Base
                        .texOffs(0, 44).addBox(4.5f, -13.0f, 4.5f, 1.0f, 1.0f, 1.0f, deformation), offset) // Arm Peg (doesn't move)

                root.addOrReplaceChild("lid", CubeListBuilder.create().texOffs(0, 27).addBox(-7.0f, -15.0f, -7.0f, 14.0f, 3.0f, 14.0f, deformation), offset)
                root.addOrReplaceChild("peg", CubeListBuilder.create().texOffs(0, 0).addBox(-0.5f, -13.0f, -0.5f, 1.0f, 1.0f, 1.0f, deformation), offset)
                root.addOrReplaceChild("arm", CubeListBuilder.create().texOffs(0, 44).addBox(4.5f, -14.0f, -2.5f, 1.0f, 1.0f, 8.0f, deformation), offset)
            }, 64, 64)
        }
    }

    private val itemRenderer: ItemRenderer
    private val model_base: ModelPart
    private val model_lid: ModelPart
    private val model_peg: ModelPart
    private val model_arm: ModelPart

    // todo: move to separate model file?
    constructor(context: BlockEntityRendererProvider.Context) {
        this.itemRenderer = context.itemRenderer
        val model = context.bakeLayer(RecordPlayerRenderer.MODEL_LAYER_LOCATION)
        this.model_base = model.getChild("base")
        this.model_lid = model.getChild("lid")
        this.model_peg = model.getChild("peg")
        this.model_arm = model.getChild("arm")
    }

    override fun render(
            entity: RecordPlayerBlockEntity,
            partialTick: Float,
            poseStack: PoseStack,
            bufferSource: MultiBufferSource,
            packedLight: Int,
            packedOverlay: Int
    ) {
        val consumer: VertexConsumer = bufferSource.getBuffer(RenderType.entityCutout(ResourceLocation(BetterRecords.ID, "textures/block/record_player.png")))
        val dir = entity.blockState.getValue(FACING).toYRot()

        poseStack.pushPose()
        poseStack.apply {
            translate(0.5f, 0.0f, 0.5f) // Move to center of block
            mulPose(Axis.XP.rotationDegrees(180f))
            mulPose(Axis.YP.rotationDegrees(dir))
        }

        this.model_base.render(poseStack, consumer, packedLight, packedOverlay)
        this.renderLid(entity, partialTick, poseStack, bufferSource, packedLight, packedOverlay)
        this.renderArm(entity, partialTick, poseStack, consumer, packedLight, packedOverlay)

        val rot = (((entity.level?.gameTime ?: 0) + partialTick) * 3f) % 360
        val toRot = if (entity.isPlaying) rot else 0f

        poseStack.pushPose()
        poseStack.mulPose(Axis.YP.rotationDegrees(toRot))
        this.model_peg.render(poseStack, consumer, packedLight, packedOverlay)
        poseStack.popPose()

        /** Might as well render anything that's in the slot,
         * so it's easier to tell what's happened in the event something else gets in somehow. */
        this.renderRecord(entity.getSlottedItem(), toRot, poseStack, bufferSource, packedLight, packedOverlay, entity.level)

        poseStack.popPose()
    }

    private fun renderLid(
            entity: RecordPlayerBlockEntity,
            partialTick: Float,
            poseStack: PoseStack,
            bufferSource: MultiBufferSource,
            packedLight: Int,
            packedOverlay: Int
    ) {
        val consumerNoCull: VertexConsumer = bufferSource.getBuffer(RenderType.armorCutoutNoCull(ResourceLocation(BetterRecords.ID, "textures/block/record_player.png")))

        var f1 = entity.getLidOpenness(partialTick)
        f1 = 1.0f - f1
        f1 = 1.0f - f1 * f1 * f1 * f1
        val lidDegrees = mapRange(0f, 1f, 0f, 60f, f1)

        poseStack.pushPose()
        poseStack.rotateAround(Axis.XN.rotationDegrees(lidDegrees), 0f, -pix(12f), pix(7f))
        this.model_lid.render(poseStack, consumerNoCull, packedLight, packedOverlay)
        poseStack.popPose()
    }

    private fun renderArm(
            entity: RecordPlayerBlockEntity,
            partialTick: Float,
            poseStack: PoseStack,
            consumer: VertexConsumer,
            packedLight: Int,
            packedOverlay: Int
    ) {
        var f1 = entity.getArmOpenness(partialTick)
        f1 = 1.0f - f1
        f1 = 1.0f - f1 * f1 * f1
        val lidDegrees = mapRange(0f, 1f, 0f, 20f, f1)

        poseStack.pushPose()
        poseStack.rotateAround(Axis.YP.rotationDegrees(lidDegrees), pix(5f), -pix(13.5f), pix(5f))
        this.model_arm.render(poseStack, consumer, packedLight, packedOverlay)
        poseStack.popPose()
    }

    private fun renderRecord(
            itemStack: ItemStack,
            rotation: Float,
            poseStack: PoseStack,
            bufferSource: MultiBufferSource,
            packedLight: Int,
            packedOverlay: Int,
            level: Level?
    ) {
        poseStack.pushPose()
        poseStack.apply {
            translate(0f, -0.76f, 0f) // 0.78125
            mulPose(Axis.XP.rotationDegrees(90f))
            mulPose(Axis.ZN.rotationDegrees(rotation))
        }

        itemRenderer.renderStatic(itemStack, ItemDisplayContext.FIXED, packedLight,
                packedOverlay, poseStack, bufferSource, level, 1)

        poseStack.popPose()
    }


}

/** For adjusting values in 16ths (1 = 1 pixel). */
private fun pix(float: Float) = (float/16)

/** My very favorite function in the whole world, MapRange, my beloved... */
private fun mapRange(fromMin: Float, fromMax: Float, toMin: Float, toMax: Float, value: Float): Float {
    return (value-fromMin) / (fromMax-fromMin) * (toMax-toMin) + toMin
}