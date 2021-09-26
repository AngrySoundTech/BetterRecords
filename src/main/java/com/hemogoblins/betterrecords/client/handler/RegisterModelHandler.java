package com.hemogoblins.betterrecords.client.handler;

import com.hemogoblins.betterrecords.BetterRecords;
import com.hemogoblins.betterrecords.block.tile.ModTiles;
import com.hemogoblins.betterrecords.client.render.RenderRecordEtcher;
import com.hemogoblins.betterrecords.client.render.RenderRecordPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(
        modid = BetterRecords.MOD_ID,
        value = Dist.CLIENT,
        bus = Mod.EventBusSubscriber.Bus.MOD
)
public class RegisterModelHandler {

    @SubscribeEvent
    public static void registerModels(FMLClientSetupEvent event) {
        ClientRegistry.bindTileEntityRenderer(ModTiles.recordPlayer, RenderRecordPlayer::new);
        ClientRegistry.bindTileEntityRenderer(ModTiles.recordEtcher, RenderRecordEtcher::new);
    }
}
