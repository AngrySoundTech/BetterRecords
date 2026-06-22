package com.hemogoblins.betterrecords.client.gui

import com.hemogoblins.betterrecords.api.client.sound.FileSoundInstance
import com.hemogoblins.betterrecords.client.sound.RecordSoundManager
import net.minecraft.Util
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.Font
import net.minecraft.client.gui.GuiGraphics
import net.minecraftforge.client.gui.overlay.ForgeGui
import net.minecraftforge.client.gui.overlay.IGuiOverlay
import kotlin.math.roundToInt

/**
 * HUD overlay for playing and downloading sounds. While a record's file is loading
 * (downloading and/or decoding) it shows a progress bar with the record name;
 * for a few seconds after playback starts it switches to a "Now Playing" line.
 */
object RecordLoadingOverlay : IGuiOverlay {

    private const val BAR_WIDTH = 120
    private const val BAR_HEIGHT = 4
    private const val PADDING = 4
    private const val GAP = 2

    /** How long "Now Playing" stays on screen after a record starts, in millis. */
    private const val NOW_PLAYING_MS = 3000L

    private const val COLOR_PANEL = 0x90000000.toInt()
    private const val COLOR_TRACK = 0xFF333333.toInt()
    private const val COLOR_FILL = 0xFF4CAF50.toInt()
    private const val COLOR_TEXT = 0xFFFFFF
    private const val COLOR_SUBTEXT = 0xAAAAAA

    override fun render(
        gui: ForgeGui,
        guiGraphics: GuiGraphics,
        partialTick: Float,
        screenWidth: Int,
        screenHeight: Int,
    ) {
        val records = RecordSoundManager.activeRecords()
        if (records.isEmpty()) return

        val font = Minecraft.getInstance().font
        val now = Util.getMillis()

        var top = PADDING
        for (sound in records) {
            top = when {
                sound.loading -> renderLoading(guiGraphics, font, sound, top)
                now - sound.startedPlayingAt < NOW_PLAYING_MS -> renderNowPlaying(guiGraphics, font, sound, top)
                else -> top
            }
        }
    }

    private fun renderLoading(guiGraphics: GuiGraphics, font: Font, sound: FileSoundInstance, top: Int): Int {
        val panelWidth = BAR_WIDTH + PADDING * 2
        val rowHeight = font.lineHeight + GAP + BAR_HEIGHT
        val progress = sound.loadProgress.coerceIn(0f, 1f)
        val percent = "${(progress * 100).roundToInt()}%"

        guiGraphics.fill(PADDING - 2, top - 2, PADDING + panelWidth, top + rowHeight + 2, COLOR_PANEL)
        guiGraphics.drawString(font, "♪ " + sound.displayName(), PADDING + 2, top, COLOR_TEXT)
        guiGraphics.drawString(font, percent, PADDING + panelWidth - font.width(percent) - 2, top, COLOR_SUBTEXT)

        val barX = PADDING + 2
        val barY = top + font.lineHeight + GAP
        guiGraphics.fill(barX, barY, barX + BAR_WIDTH, barY + BAR_HEIGHT, COLOR_TRACK)
        val filled = (BAR_WIDTH * progress).roundToInt().coerceIn(0, BAR_WIDTH)
        guiGraphics.fill(barX, barY, barX + filled, barY + BAR_HEIGHT, COLOR_FILL)

        return top + rowHeight + PADDING + GAP
    }

    private fun renderNowPlaying(guiGraphics: GuiGraphics, font: Font, sound: FileSoundInstance, top: Int): Int {
        val label = "♪ Now Playing: " + sound.displayName()
        val panelWidth = font.width(label) + PADDING * 2

        guiGraphics.fill(PADDING - 2, top - 2, PADDING + panelWidth, top + font.lineHeight + 2, COLOR_PANEL)
        guiGraphics.drawString(font, label, PADDING + 2, top, COLOR_TEXT)

        return top + font.lineHeight + PADDING + GAP
    }

    private fun FileSoundInstance.displayName(): String = name.ifEmpty { "Record" }
}
