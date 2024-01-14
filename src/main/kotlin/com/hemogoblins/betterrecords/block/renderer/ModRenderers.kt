package com.hemogoblins.betterrecords.block.renderer

import com.hemogoblins.betterrecords.block.ModBlocks
import net.minecraftforge.client.event.EntityRenderersEvent
import net.minecraftforge.eventbus.api.IEventBus
import net.minecraftforge.eventbus.api.SubscribeEvent

object ModRenderers {

    @SubscribeEvent
    fun registerRenderers(ere: EntityRenderersEvent.RegisterRenderers) {
        ere.registerBlockEntityRenderer(ModBlocks.RECORD_ETCHER_ENTITY.get(), ::RecordEtcherRenderer)
    }

    @SubscribeEvent
    fun registerLayers(ere: EntityRenderersEvent.RegisterLayerDefinitions) {
        ere.registerLayerDefinition(RecordEtcherRenderer.MODEL_LAYER_LOCATION) { RecordEtcherRenderer.createBodyLayer() }
    }

    fun register(eventBus: IEventBus) {
        eventBus.register(this)
    }
}