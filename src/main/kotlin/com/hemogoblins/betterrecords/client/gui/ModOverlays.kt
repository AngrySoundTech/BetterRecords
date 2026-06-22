package com.hemogoblins.betterrecords.client.gui

import net.minecraftforge.client.event.RegisterGuiOverlaysEvent
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay

/**
 * Registers the mod's HUD overlays. Wired up on the client from
 * [com.hemogoblins.betterrecords.BetterRecords].
 */
object ModOverlays {

    fun register(event: RegisterGuiOverlaysEvent) {
        event.registerAbove(VanillaGuiOverlay.HOTBAR.id(), "record_loading", RecordLoadingOverlay)
    }
}
