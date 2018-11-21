package com.codingforcookies.betterrecords.client

import com.codingforcookies.betterrecords.CommonProxy
import com.codingforcookies.betterrecords.helper.nbt.getColor
import com.codingforcookies.betterrecords.item.ModItem
import com.codingforcookies.betterrecords.item.ModItems
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.color.IItemColor
import net.minecraft.item.Item
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent

class ClientProxy : CommonProxy() {

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