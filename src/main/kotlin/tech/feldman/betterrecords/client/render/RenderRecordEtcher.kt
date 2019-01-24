package tech.feldman.betterrecords.client.render

import tech.feldman.betterrecords.ID
import tech.feldman.betterrecords.block.tile.TileRecordEtcher
import tech.feldman.betterrecords.client.model.ModelRecordEtcher
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager.*
import net.minecraft.client.renderer.block.model.ItemCameraTransforms
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer
import net.minecraft.util.ResourceLocation

class RenderRecordEtcher : TileEntitySpecialRenderer<TileRecordEtcher>() {

    val MODEL = ModelRecordEtcher()
    val TEXTURE = ResourceLocation(ID, "textures/models/recordetcher.png")

    override fun render(te: TileRecordEtcher?, x: Double, y: Double, z: Double, partialTicks: Float, destroyStage: Int, alpha: Float) {

        te?.recordEntity?.let {
            pushMatrix()

            translate(x + 0.5F, y + .65F, z + 0.5F)
            rotate(90F, 1F, 0F, 0F)
            rotate(te.recordRotation * 57.3F, 0F, 0F, 1F)

            Minecraft.getMinecraft().renderItem.renderItem(it.item, ItemCameraTransforms.TransformType.NONE)

            popMatrix()
        }

        pushMatrix()
        translate(x + 0.5F, y + 1.5F, z + 0.5F)
        rotate(180F, 0F, 0F, 1F)
        bindTexture(TEXTURE)

        val needleLocation = te?.needleLocation ?: 0F
        val recordRotation = te?.recordRotation ?: 0F
        MODEL.render(null, needleLocation, recordRotation, 0F, 0F, 0F, 0.0625F)

        popMatrix()
    }
}
