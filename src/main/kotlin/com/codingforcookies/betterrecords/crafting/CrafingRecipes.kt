package com.codingforcookies.betterrecords.crafting

import com.codingforcookies.betterrecords.ID
import com.codingforcookies.betterrecords.crafting.recipe.RecipeColoredRecord
import com.codingforcookies.betterrecords.crafting.recipe.RecipeMultiRecord
import com.codingforcookies.betterrecords.crafting.recipe.RecipeRepeatable
import com.codingforcookies.betterrecords.crafting.recipe.RecipeShufflable
import net.minecraft.item.crafting.IRecipe
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

@Mod.EventBusSubscriber(modid = ID)
object CrafingRecipes {

    @SubscribeEvent
    fun registerRecipes(event: RegistryEvent.Register<IRecipe>) {
        event.registry.registerAll(
                RecipeMultiRecord(),
                RecipeRepeatable(),
                RecipeShufflable(),
                RecipeColoredRecord()
        )
    }
}
