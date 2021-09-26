package com.hemogoblins.betterrecords.client.render;

import com.hemogoblins.betterrecords.block.tile.TileRecordPlayer;
import com.hemogoblins.betterrecords.client.model.ModelRecordPlayer;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Quaternion;

import javax.annotation.Nullable;

public class RenderRecordPlayer extends TileEntityRenderer<TileRecordPlayer> {

    private static final ResourceLocation TEXTURE = new ResourceLocation("betterrecords:textures/model/recordplayer.png");
    private static final ModelRecordPlayer MODEL = new ModelRecordPlayer();

    public RenderRecordPlayer(TileEntityRendererDispatcher manager) {
        super(manager);
    }

    @Override
    public void render(@Nullable TileRecordPlayer recordPlayer, float partialTicks, MatrixStack ms, IRenderTypeBuffer buffers, int light, int overlay) {
        /// System.out.println("RENDER RENDER RENDER");

        IVertexBuilder buffer = buffers.getBuffer(MODEL.getRenderType(TEXTURE));

        //region RENDER_BLOCK
        ms.push();

        ms.translate(0.5f, 1.5f, 0.5f);
        ms.rotate(new Quaternion(180f, 0.0f, 0.0f, 1.0f));

//        if (recordPlayer != null) {
//            switch (recordPlayer.getBlockState().get(BlockStateProperties.HORIZONTAL_FACING)) {
//                case WEST: ms.rotate(Vector3f.YP.rotationDegrees(90f));
//                case NORTH: ms.rotate(Vector3f.YP.rotationDegrees(180f));
//                case EAST: ms.rotate(Vector3f.YP.rotationDegrees(270f));
//                case SOUTH: break;
//            }
//        }

        float openAmount = 0f;
        float needleLocation = 0f;
        float recordLocation = 0f;
        MODEL.render(ms, buffer, light, overlay, openAmount, needleLocation, recordLocation, 0.0625f);

        ms.pop();
        //endregion RENDER_BLOCK

        // TODO: Render Record
    }
}
