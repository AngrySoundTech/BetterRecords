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
package tech.feldman.betterrecords.crafting.recipe

import tech.feldman.betterrecords.api.sound.IRepeatableSoundHolder
import tech.feldman.betterrecords.helper.nbt.setRepeatable
import tech.feldman.betterrecords.item.ModItems
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
            setRepeatable(this, true)
        }
    }

    override fun getRecipeOutput() = ItemStack(ModItems.itemRecord)

    override fun getRemainingItems(inv: InventoryCrafting): NonNullList<ItemStack> {
        inv.clear()
        return NonNullList.create()
    }
}
