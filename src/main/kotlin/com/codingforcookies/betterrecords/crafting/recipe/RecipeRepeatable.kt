package com.codingforcookies.betterrecords.crafting.recipe

import com.codingforcookies.betterrecords.api.sound.IRepeatableSoundHolder
import com.codingforcookies.betterrecords.item.ModItems
import net.minecraft.init.Items
import net.minecraft.inventory.InventoryCrafting
import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.IRecipe
import net.minecraft.util.NonNullList
import net.minecraft.world.World
import net.minecraftforge.registries.IForgeRegistryEntry

class RecipeRepeatable : IForgeRegistryEntry.Impl<IRecipe>(), IRecipe {

    init {
        setRegistryName("reciperepeatable")
    }

    override fun canFit(width: Int, height: Int) = width * height >= 2

    override fun matches(inventoryCrafting: InventoryCrafting, worldIn: World?): Boolean {
        var foundRepeatable = false
        var foundComparator = false

        (0 until inventoryCrafting.sizeInventory)
                .map { inventoryCrafting.getStackInSlot(it) }
                .filter { !it.isEmpty }
                .forEach {
                    if (it.item is IRepeatableSoundHolder && !foundRepeatable) {
                        foundRepeatable = true
                    } else if (it.item == Items.COMPARATOR && !foundComparator) {
                        foundComparator = true
                    } else {
                        return false
                    }
                }

        return foundRepeatable && foundComparator
    }

    override fun getCraftingResult(inventoryCrafting: InventoryCrafting): ItemStack? {
        val repeatable =
                (0 until inventoryCrafting.sizeInventory)
                        .map { inventoryCrafting.getStackInSlot(it) }
                        .filter { !it.isEmpty }
                        .find { it.item is IRepeatableSoundHolder }

        return repeatable?.copy()?.apply {
            (this.item as IRepeatableSoundHolder).setRepeatable(this, true)
        }
    }

    override fun getRecipeOutput() = ItemStack(ModItems.itemNewRecord)

    override fun getRemainingItems(inv: InventoryCrafting): NonNullList<ItemStack> {
        inv.clear()
        return NonNullList.create()
    }
}
