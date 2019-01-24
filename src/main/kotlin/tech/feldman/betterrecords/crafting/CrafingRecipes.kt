package tech.feldman.betterrecords.crafting

import tech.feldman.betterrecords.ID
import tech.feldman.betterrecords.crafting.recipe.RecipeColoredRecord
import tech.feldman.betterrecords.crafting.recipe.RecipeMultiRecord
import tech.feldman.betterrecords.crafting.recipe.RecipeRepeatable
import tech.feldman.betterrecords.crafting.recipe.RecipeShufflable
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
