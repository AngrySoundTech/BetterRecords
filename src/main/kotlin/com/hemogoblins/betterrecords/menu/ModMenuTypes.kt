package com.hemogoblins.betterrecords.menu

import com.hemogoblins.betterrecords.BetterRecords
import net.minecraft.world.flag.FeatureFlagSet
import net.minecraft.world.inventory.MenuType
import net.minecraftforge.common.extensions.IForgeMenuType
import net.minecraftforge.eventbus.api.IEventBus
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.ForgeRegistries
import net.minecraftforge.registries.RegistryObject
import java.util.function.Supplier

object ModMenuTypes {

    val MENU_TYPES = DeferredRegister.create(ForgeRegistries.MENU_TYPES, BetterRecords.ID)

    val RECORD_ETCHER_MENU = registerMenu("record_etcher") {
        MenuType(::RecordEtcherMenu, FeatureFlagSet.of())
    }

    fun register(eventBus: IEventBus) {
        MENU_TYPES.register(eventBus)
    }

    private fun <T> registerMenu(name: String, menu: Supplier<T>): RegistryObject<T> where T: MenuType<*> {
        return MENU_TYPES.register(name, menu)
    }
}