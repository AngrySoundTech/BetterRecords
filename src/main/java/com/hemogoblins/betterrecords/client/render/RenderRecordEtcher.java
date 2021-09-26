package com.hemogoblins.betterrecords.client.render;

import com.hemogoblins.betterrecords.BetterRecords;
import com.hemogoblins.betterrecords.block.tile.TileRecordEtcher;
import com.hemogoblins.betterrecords.client.model.ModelRecordEtcher;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;

public class RenderRecordEtcher extends TileEntityRenderer<TileRecordEtcher> {

    private static final ResourceLocation TEXTURE = new ResourceLocation(BetterRecords.MOD_ID, "textures/model/record_etcher.png");
    private static final ModelRecordEtcher MODEL = new ModelRecordEtcher();

    public RenderRecordEtcher(TileEntityRendererDispatcher manager) {
        super(manager);
    }

    @Override
    public void render(@Nullable TileRecordEtcher recordEtcher, float partialTicks, MatrixStack ms, IRenderTypeBuffer buffers, int light, int overlay) {
        //region RENDER_BLOCK
        ms.push();

        ms.translate(0.5f, 1.5f, 0.5f);
        ms.scale(1F, -1F, -1F);

//        if (recordEtcher != null) {
//            switch (recordEtcher.getBlockState().get(BlockStateProperties.HORIZONTAL_FACING)) {
//                case WEST: ms.rotate(Vector3f.YP.rotationDegrees(90f));
//                case NORTH: ms.rotate(Vector3f.YP.rotationDegrees(180f));
//                case EAST: ms.rotate(Vector3f.YP.rotationDegrees(270f));
//                case SOUTH: break;
//            }
//        }

        float needleLocation = 0f;
        float recordLocation = 0f;

        IVertexBuilder buffer = buffers.getBuffer(MODEL.getRenderType(TEXTURE));
        MODEL.render(ms, buffer, light, overlay, 1, 1, 1, 1, needleLocation, recordLocation);

        ms.pop();
        //endregion RENDER_BLOCK

        // TODO: Render Record
    }
}
