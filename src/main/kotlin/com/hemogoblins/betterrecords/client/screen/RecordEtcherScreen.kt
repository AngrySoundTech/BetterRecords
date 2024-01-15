package com.hemogoblins.betterrecords.client.screen

import com.hemogoblins.betterrecords.BetterRecords
import com.hemogoblins.betterrecords.menu.RecordEtcherMenu
import com.hemogoblins.betterrecords.network.ModNetwork
import com.hemogoblins.betterrecords.network.serverbound.RequestEtchPacket
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.Button
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

    val etchButton = Button.builder(Component.translatable("menu.${BetterRecords.ID}.record_etcher.etch")) {
        ModNetwork.channel.sendToServer(RequestEtchPacket(container.blockEntity.blockPos, "Fast Car"))
    }
        .pos(guiLeft + 175, guiTop + 40 )
        .width(20)
        .build()

    override fun init() {
        super.init()
        addRenderableWidget(etchButton)
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
