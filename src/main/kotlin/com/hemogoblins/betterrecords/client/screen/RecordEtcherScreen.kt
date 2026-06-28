package com.hemogoblins.betterrecords.client.screen

import com.hemogoblins.betterrecords.BetterRecords
import com.hemogoblins.betterrecords.client.cache.UnsupportedAudioTypeException
import com.hemogoblins.betterrecords.menu.RecordEtcherMenu
import com.hemogoblins.betterrecords.network.ModNetwork
import com.hemogoblins.betterrecords.network.serverbound.RequestEtchPacket
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.Button
import net.minecraft.client.gui.components.EditBox
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.player.Inventory
import org.lwjgl.glfw.GLFW
import kotlin.math.roundToInt

class RecordEtcherScreen(
    container: RecordEtcherMenu,
    playerInventory: Inventory,
    title: Component,
) : AbstractContainerScreen<RecordEtcherMenu>(container, playerInventory, title) {

    private val BACKGROUND_TEXTURE = ResourceLocation(BetterRecords.ID, "textures/gui/record_etcher.png")

    private val ERROR_COLOR = 0xFF5555
    private val SUCCESS_COLOR = 0x55FF55
    private val BAR_TRACK_COLOR = 0xFF333333.toInt()
    private val BAR_FILL_COLOR = 0xFF4CAF50.toInt()

    private lateinit var urlBox: EditBox
    private lateinit var nameBox: EditBox
    private lateinit var etchButton: Button

    /** True once the player has manually edited the name, so we stop autofilling it from the URL */
    private var nameEdited = false
    private var settingNameProgrammatically = false

    /** Feedback message */
    @Volatile
    private var statusMessage: Component? = null

    /** Color the current [statusMessage] is drawn in */
    @Volatile
    private var statusColor = ERROR_COLOR

    /** True while a song is being downloaded */
    @Volatile
    private var downloading = false

    /** Download progress in the range 0..1 */
    @Volatile
    private var downloadProgress = 0f

    init {
        imageHeight = 202
        inventoryLabelY = imageHeight - 94
    }

    override fun init() {
        super.init()

        urlBox = EditBox(
            font,
            leftPos + 8,
            topPos + 44,
            imageWidth - 16,
            16,
            Component.translatable("menu.${BetterRecords.ID}.record_etcher.url"),
        ).apply {
            setMaxLength(512)
            setHint(Component.translatable("menu.${BetterRecords.ID}.record_etcher.url"))
            setResponder { onUrlChanged(it) }
        }

        nameBox = EditBox(
            font,
            leftPos + 8,
            topPos + 62,
            imageWidth - 16,
            16,
            Component.translatable("menu.${BetterRecords.ID}.record_etcher.name"),
        ).apply {
            setMaxLength(128)
            setHint(Component.translatable("menu.${BetterRecords.ID}.record_etcher.name"))
            // Disabled until a URL is entered.
            active = false
            setResponder {
                if (!settingNameProgrammatically) nameEdited = true
            }
        }

        addRenderableWidget(urlBox)
        addRenderableWidget(nameBox)

        etchButton = Button.builder(Component.translatable("menu.${BetterRecords.ID}.record_etcher.etch")) { onEtch() }
            .pos(leftPos + (imageWidth - 60) / 2, topPos + 80)
            .size(60, 18)
            .build()
        addRenderableWidget(etchButton)
    }

    /**
     * Keep the name box enabled/disabled with the URL
     * until the player edits the name themselves, then keeps it filled with the name derived from the current URL.
     */
    private fun onUrlChanged(rawUrl: String) {
        val url = rawUrl.trim()
        nameBox.active = url.isNotEmpty()

        if (!nameEdited) {
            settingNameProgrammatically = true
            nameBox.value = if (url.isEmpty()) "" else nameFromUrl(url)
            settingNameProgrammatically = false
        }
    }

    private fun onEtch() {
        val url = urlBox.value.trim()
        if (url.isEmpty()) {
            setError("empty_url")
            return
        }
        if (!menu.slots.last().hasItem()) {
            setError("no_record")
            return
        }

        statusMessage = null
        etchButton.active = false
        downloading = true
        downloadProgress = 0f

        val pos = menu.blockEntity.blockPos
        val name = nameBox.value.trim().ifBlank { nameFromUrl(url) }

        BetterRecords.cache.download(url) { percent ->
            downloadProgress = (percent / 100f).coerceIn(0f, 1f)
        }.whenComplete { cacheEntry, error ->
            downloading = false
            etchButton.active = true
            if (error != null) {
                val unsupported = generateSequence(error) { it.cause.takeIf { c -> c !== it } }
                    .filterIsInstance<UnsupportedAudioTypeException>()
                    .firstOrNull()
                if (unsupported != null) {
                    setError("not_audio", unsupported.contentType)
                } else {
                    setError("download_failed")
                }
                BetterRecords.logger.error("Failed to download record for etching", error)
                return@whenComplete
            }

            ModNetwork.channel.sendToServer(
                RequestEtchPacket(
                    pos,
                    name = name,
                    url = url,
                    checksum = cacheEntry.checksum,
                )
            )

            statusMessage = Component.translatable("menu.${BetterRecords.ID}.record_etcher.success", name)
            statusColor = SUCCESS_COLOR
        }
    }

    override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
        // Escape should always let us out.
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            return super.keyPressed(keyCode, scanCode, modifiers)
        }
        // Focused text box swallows key presses, so we don't for example hit e and close the gui
        if (urlBox.keyPressed(keyCode, scanCode, modifiers) || urlBox.canConsumeInput() ||
            nameBox.keyPressed(keyCode, scanCode, modifiers) || nameBox.canConsumeInput()
        ) {
            return true
        }
        return super.keyPressed(keyCode, scanCode, modifiers)
    }

    private fun setError(key: String, vararg args: Any) {
        statusMessage = Component.translatable("menu.${BetterRecords.ID}.record_etcher.error.$key", *args)
        statusColor = ERROR_COLOR
    }

    /** Derives a human-readable record name from the file portion of [url].
     * TODO: this can bear to be improved
     */
    private fun nameFromUrl(url: String): String {
        val fileName = url.substringBefore('?').substringAfterLast('/').substringBeforeLast('.')
        return fileName.ifBlank { "Record" }
    }

    override fun render(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTicks: Float) {
        this.renderBackground(guiGraphics)
        super.render(guiGraphics, mouseX, mouseY, partialTicks)
        this.renderTooltip(guiGraphics, mouseX, mouseY)
    }

    override fun renderLabels(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int) {
        super.renderLabels(guiGraphics, mouseX, mouseY)

        if (downloading) {
            renderProgressBar(guiGraphics)
        } else {
            statusMessage?.let {
                val x = (imageWidth - font.width(it)) / 2
                guiGraphics.drawString(font, it, x, 100, statusColor, false)
            }
        }
    }

    private fun renderProgressBar(guiGraphics: GuiGraphics) {
        val barX = 8
        val barY = 100
        val barWidth = imageWidth - 16
        val barHeight = 6
        val progress = downloadProgress.coerceIn(0f, 1f)

        guiGraphics.fill(barX, barY, barX + barWidth, barY + barHeight, BAR_TRACK_COLOR)
        val filled = (barWidth * progress).roundToInt().coerceIn(0, barWidth)
        guiGraphics.fill(barX, barY, barX + filled, barY + barHeight, BAR_FILL_COLOR)
    }

    override fun renderBg(guiGraphics: GuiGraphics, partialTicks: Float, x: Int, y: Int) {
        val k = (width - imageWidth) / 2
        val l = (height - imageHeight) / 2
        guiGraphics.blit(BACKGROUND_TEXTURE, k, l, 0, 0, imageWidth, imageHeight)
    }
}
