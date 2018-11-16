package com.codingforcookies.betterrecords.crafting.recipe

import com.codingforcookies.betterrecords.api.sound.ISoundHolder
import com.codingforcookies.betterrecords.item.ItemRecord
import com.codingforcookies.betterrecords.item.ModItems
import net.minecraft.inventory.InventoryCrafting
import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.IRecipe
import net.minecraft.util.NonNullList
import net.minecraft.world.World
import net.minecraftforge.registries.IForgeRegistryEntry

import java.util.ArrayList

class RecipeMultiRecord : IForgeRegistryEntry.Impl<IRecipe>(), IRecipe {

    init {
        setRegistryName("recipemultirecord")
    }

    override fun canFit(width: Int, height: Int) = width * height >= 2

    override fun matches(inventoryCrafting: InventoryCrafting, worldIn: World?): Boolean {
        var count = 0

        (0 until inventoryCrafting.sizeInventory)
                .asSequence()
                .map { inventoryCrafting.getStackInSlot(it) }
                .filter { !it.isEmpty }
                .forEach {
                    if (it.item is ItemRecord && (it.item as ISoundHolder).getSounds(it).isNotEmpty()) {
                        count++
                    } else {
                        return false
                    }
                }

        return count > 1
    }

    override fun getCraftingResult(inventoryCrafting: InventoryCrafting): ItemStack? {
        val records = ArrayList<ItemStack>()

        (0 until inventoryCrafting.sizeInventory)
                .map { inventoryCrafting.getStackInSlot(it) }
                .filter { !it.isEmpty }
                .forEach {
                    records.add(it)
                }

        return ItemStack(ModItems.itemNewRecord).apply {
            records.forEach {
                (it.item as ISoundHolder).getSounds(it).forEach {
                    (this.item as ISoundHolder).addSound(this, it)
                }
            }
        }
    }

    override fun getRecipeOutput() = ItemStack(ModItems.itemNewRecord)

    override fun getRemainingItems(inv: InventoryCrafting): NonNullList<ItemStack> {
        inv.clear()
        return NonNullList.create()
    }
}
