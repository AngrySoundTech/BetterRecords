package com.hemogoblins.betterrecords.item

import com.hemogoblins.betterrecords.BetterRecords
import net.minecraft.client.color.item.ItemColor
import net.minecraft.core.registries.Registries
import net.minecraft.network.chat.Component
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraftforge.client.event.RegisterColorHandlersEvent
import net.minecraftforge.eventbus.api.IEventBus
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.ForgeRegistries
import net.minecraftforge.registries.RegistryObject
import java.util.function.Supplier

object ModItems {

    val ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, BetterRecords.ID)
    val CREATIVE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, BetterRecords.ID)

    val record = registerItem("record") {
        Item(Item.Properties())
    }

    val CREATIVE_TAB = CREATIVE_TABS.register(BetterRecords.ID) {
        with(CreativeModeTab.builder()) {
            title(Component.translatable("item_group.${BetterRecords.ID}"))
            icon { ItemStack(record.get()) }

            displayItems { params, output ->
                output.apply {
                    accept(record.get())
                    // accept(ModBlocks.TEST_BLOCK.get())
                }
            }

            build()
        }
    }

    @SubscribeEvent
    fun registerItemColors(event: RegisterColorHandlersEvent.Item) {
        val color = ItemColor { stack, tintIndex ->
            if (tintIndex > 0) {
                if (stack.tag != null) stack.tag!!.getInt("color") else -1
            } else {
                0xFFFFFF
            }
        }

        event.register(color, record.get())
    }

    fun register(eventBus: IEventBus) {
        eventBus.register(this)
        ITEMS.register(eventBus)
        CREATIVE_TABS.register(eventBus)
    }

    fun <T> registerItem(name: String, item: Supplier<T>): RegistryObject<T> where T : Item {
        return ITEMS.register(name, item)
    }
}