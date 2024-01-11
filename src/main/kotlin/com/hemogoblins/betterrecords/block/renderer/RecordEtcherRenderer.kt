package com.hemogoblins.betterrecords.block.renderer

import com.hemogoblins.betterrecords.block.entity.RecordEtcherBlockEntity
import com.hemogoblins.betterrecords.item.ModItems
import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider
import net.minecraft.client.renderer.entity.ItemRenderer
import net.minecraft.world.item.ItemStack
import net.minecraft.world.phys.Vec3
import com.mojang.math.Axis
import net.minecraft.client.renderer.LevelRenderer
import net.minecraft.world.item.ItemDisplayContext

class RecordEtcherRenderer: BlockEntityRenderer<RecordEtcherBlockEntity> {

    val itemRenderer: ItemRenderer

    constructor(context: BlockEntityRendererProvider.Context) {
        this.itemRenderer = context.itemRenderer
    }

    override fun render(
            etcher: RecordEtcherBlockEntity,
            partialTick: Float,
            poseStack: PoseStack,
            bufferSource: MultiBufferSource,
            packedLight: Int,
            packedOverlay: Int
    ) {
        val itemStack = etcher.getSlottedRecord()
        if (!itemStack.isEmpty) {
            renderRecord(itemStack, calcRotation(partialTick, etcher),
                    etcher, poseStack, bufferSource, packedLight, packedOverlay)
        }

        //TODO: nonlinear record anim (from TE state)
        //TODO: animate needle
    }

    fun calcRotation(partialTick: Float, etcher: RecordEtcherBlockEntity): Float {
        val speed = 3.0
        val time = (etcher.level?.gameTime ?: 0) + partialTick

        return (time * speed).toFloat() % 360
    }

    fun renderRecord(
            itemStack: ItemStack,
            rotation: Float,
            etcher: RecordEtcherBlockEntity,
            poseStack: PoseStack,
            bufferSource: MultiBufferSource,
            packedLight: Int,
            packedOverlay: Int
    ) {
        poseStack.pushPose()
        poseStack.translate(0.5f, 0.65f, 0.5f)
        poseStack.mulPose(Axis.XP.rotationDegrees(90f))
        poseStack.mulPose(Axis.ZP.rotationDegrees(rotation))

        itemRenderer.renderStatic(
                itemStack,
                ItemDisplayContext.FIXED,
                packedLight,
                packedOverlay,
                poseStack,
                bufferSource,
                etcher.level,
                1)

        poseStack.popPose()
    }
}