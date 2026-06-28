package com.hemogoblins.betterrecords.block.renderer

import com.hemogoblins.betterrecords.capability.ColorableCapability
import com.hemogoblins.betterrecords.capability.ModCapabilities
import com.hemogoblins.betterrecords.item.ModItems
import net.minecraft.client.renderer.entity.ItemRenderer
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level

/**
 * Creates proxy record itemstacks for rendering purposes out of vanilla discs.
 * Samples the color of the disc, and stores it for future plays.
 */
object VanillaDiscProxies {

    private const val DEFAULT_COLOR = 0xFFFFFF

    private val proxies = mutableMapOf<Item, ItemStack>()

    fun proxyFor(stack: ItemStack, itemRenderer: ItemRenderer, level: Level?): ItemStack {
        return proxies.getOrPut(stack.item) {
            val color = sampleDiscColor(stack, itemRenderer, level)

            ItemStack(ModItems.RECORD.get()).apply {
                getCapability(ModCapabilities.COLORABLE_CAPABILITY).ifPresent {
                    (it as ColorableCapability).color = color
                }
            }
        }
    }

    private fun sampleDiscColor(stack: ItemStack, itemRenderer: ItemRenderer, level: Level?): Int {
        return try {
            val sprite = itemRenderer.getModel(stack, level, null, 0).particleIcon
            val contents = sprite.contents()
            val image = contents.originalImage

            var rAcc = 0.0
            var gAcc = 0.0
            var bAcc = 0.0
            var weight = 0.0

            for (y in 0 until contents.height()) {
                for (x in 0 until contents.width()) {
                    val argb = image.getPixelRGBA(x, y) // NativeImage is little-endian 0xAABBGGRR
                    val a = (argb ushr 24) and 0xFF
                    if (a < 128) continue

                    val r = argb and 0xFF
                    val g = (argb ushr 8) and 0xFF
                    val b = (argb ushr 16) and 0xFF

                    val max = maxOf(r, g, b)
                    if (max == 0) continue
                    val min = minOf(r, g, b)

                    val saturation = (max - min).toDouble() / max
                    val w = saturation * saturation * max
                    rAcc += r * w
                    gAcc += g * w
                    bAcc += b * w
                    weight += w
                }
            }

            if (weight <= 0.0) return DEFAULT_COLOR

            val r = (rAcc / weight).toInt().coerceIn(0, 255)
            val g = (gAcc / weight).toInt().coerceIn(0, 255)
            val b = (bAcc / weight).toInt().coerceIn(0, 255)
            (r shl 16) or (g shl 8) or b
        } catch (e: Exception) {
            DEFAULT_COLOR
        }
    }
}
