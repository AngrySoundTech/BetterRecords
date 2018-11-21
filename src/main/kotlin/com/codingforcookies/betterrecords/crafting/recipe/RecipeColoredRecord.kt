package com.codingforcookies.betterrecords.crafting.recipe

import com.codingforcookies.betterrecords.api.sound.IColorableSoundHolder
import com.codingforcookies.betterrecords.helper.nbt.setColor
import net.minecraft.init.Blocks
import net.minecraft.init.Items
import net.minecraft.inventory.InventoryCrafting
import net.minecraft.item.EnumDyeColor
import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.IRecipe
import net.minecraft.util.NonNullList
import net.minecraft.world.World
import net.minecraftforge.registries.IForgeRegistryEntry

class RecipeColoredRecord : IForgeRegistryEntry.Impl<IRecipe>(), IRecipe {

    init {
        setRegistryName("recipecolorable")
    }

    override fun canFit(width: Int, height: Int) = width * height >= 2

    override fun matches(inventoryCrafting: InventoryCrafting, world: World?): Boolean {
        var foundColorable = false
        var foundDye = false

        (0 until inventoryCrafting.sizeInventory)
                .map { inventoryCrafting.getStackInSlot(it) }
                .filter { !it.isEmpty }
                .forEach {
                    if (it.item is IColorableSoundHolder && !foundColorable) {
                        foundColorable = true
                    } else if (it.item == Items.DYE && !foundDye) {
                        foundDye = true
                    } else {
                        return false
                    }
                }

        return foundColorable && foundDye
    }

    override fun getCraftingResult(inventoryCrafting: InventoryCrafting): ItemStack? {
        var colorable: ItemStack? = null
        var color = -1

        (0 until inventoryCrafting.sizeInventory)
                .map { inventoryCrafting.getStackInSlot(it) }
                .filter { !it.isEmpty }
                .forEach {
                    if (it.item is IColorableSoundHolder && colorable == null) {
                        colorable = it
                    } else {
                        color = EnumDyeColor.byDyeDamage(it.itemDamage).colorValue
                    }
                }

        return colorable?.copy()?.apply {
            setColor(this, color)
        }
    }

    override fun getRecipeOutput() = ItemStack(Blocks.AIR)

    override fun getRemainingItems(inv: InventoryCrafting): NonNullList<ItemStack> {
        inv.clear()
        return NonNullList.create()
    }
}
