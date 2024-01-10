package com.hemogoblins.betterrecords.client.screen

import com.hemogoblins.betterrecords.BetterRecords
import com.hemogoblins.betterrecords.menu.RecordEtcherMenu
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.player.Inventory

class RecordEtcherScreen(
    container: RecordEtcherMenu,
    playerInventory: Inventory,
    title: Component,
) : AbstractContainerScreen<RecordEtcherMenu>(container, playerInventory, title) {

    private val BACKGROUND_TEXTURE = ResourceLocation(BetterRecords.ID, "textures/gui/record_etcher.png")

    init {
        imageHeight = 202
        inventoryLabelY = imageHeight - 94
    }

    override fun render(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTicks: Float) {
        this.renderBackground(guiGraphics)
        super.render(guiGraphics, mouseX, mouseY, partialTicks)
        this.renderTooltip(guiGraphics, mouseX, mouseY)
    }

    override fun renderBg(guiGraphics: GuiGraphics, partialTicks: Float, x: Int, y: Int) {
        val k = (width - imageWidth) / 2
        val l = (height - imageHeight) / 2
        guiGraphics.blit(BACKGROUND_TEXTURE, k, l, 0, 0, imageWidth, imageHeight)
    }
}
