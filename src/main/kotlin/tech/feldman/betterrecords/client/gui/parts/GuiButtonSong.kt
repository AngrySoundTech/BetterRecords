/**
 * The MIT License
 *
 * Copyright (c) 2019 Nicholas Feldman
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package tech.feldman.betterrecords.client.gui.parts

import tech.feldman.betterrecords.api.library.Song
import tech.feldman.betterrecords.extensions.glVertices
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager
import net.minecraftforge.fml.client.config.GuiButtonExt
import org.lwjgl.opengl.GL11
import java.awt.Color

class GuiButtonSong(id: Int, xPos: Int, yPos: Int, width: Int, height: Int, displayString: String, var entry: Song)
    : GuiButtonExt(id, xPos, yPos, width, height, displayString) {

    override fun drawButton(mc: Minecraft, mouseX: Int, mouseY: Int, partial: Float) {
        super.drawButton(mc, mouseX, mouseY, partial)

        if (visible) {
            GlStateManager.disableTexture2D()
            glVertices(GL11.GL_QUADS) {
                with(Color(entry.colorInt)) {
                    GlStateManager.color(red / 255F, green / 255F, blue / 255F)
                }

                val topY = y + 2 // two from the top
                val bottomY = y + height - 3 // three from the bottom
                val rightX = x + width - 2 // two from the right
                val leftX = rightX - 2 // Draw two wide

                GL11.glVertex2i(leftX, topY) // Top Left
                GL11.glVertex2i(leftX, bottomY) // Bottom Left
                GL11.glVertex2i(rightX, bottomY) // Bottom Right
                GL11.glVertex2i(rightX, topY) // Top Right
            }
            GlStateManager.enableTexture2D()
        }
    }

}