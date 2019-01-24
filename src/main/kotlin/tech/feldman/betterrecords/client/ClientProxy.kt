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
package tech.feldman.betterrecords.client

import tech.feldman.betterrecords.CommonProxy
import tech.feldman.betterrecords.helper.nbt.getColor
import tech.feldman.betterrecords.item.ModItem
import tech.feldman.betterrecords.item.ModItems
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.color.IItemColor
import net.minecraft.item.Item
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent

class ClientProxy : tech.feldman.betterrecords.CommonProxy() {

    companion object {
        var encodings = mutableListOf(
                "audio/ogg", "application/ogg",
                "audio/mp3",
                "audio/mpeg", "audio/mpeg; charset=UTF-8",
                "application/octet-stream",
                "audio/wav", "audio/x-wav")
    }

    override fun init(event: FMLInitializationEvent) {
        super.init(event)

        Item.REGISTRY
                .filterIsInstance<ModItem>()
                .forEach(ModItem::registerRender)
    }

    override fun postInit(event: FMLPostInitializationEvent) {
        super.postInit(event)

        val color = IItemColor { stack, tintIndex ->
            if (tintIndex > 0) {
                getColor(stack)
            } else {
                0xFFFFFF
            }
        }

        Minecraft.getMinecraft().itemColors.run {
            registerItemColorHandler(color, ModItems.itemRecord)
            registerItemColorHandler(color, ModItems.itemFrequencyCrystal)
        }
    }
}