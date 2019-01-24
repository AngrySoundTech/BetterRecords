package tech.feldman.betterrecords.crafting.recipe

import tech.feldman.betterrecords.helper.nbt.addSound
import tech.feldman.betterrecords.helper.nbt.getSounds
import tech.feldman.betterrecords.item.ItemRecord
import tech.feldman.betterrecords.item.ModItems
import net.minecraft.inventory.InventoryCrafting
import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.IRecipe
import net.minecraft.util.NonNullList
import net.minecraft.world.World
import net.minecraftforge.registries.IForgeRegistryEntry
import java.util.*

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
                    if (it.item is ItemRecord && getSounds(it).isNotEmpty()) {
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

        return ItemStack(ModItems.itemRecord).apply {
            records.forEach {
                getSounds(it).forEach {
                    addSound(this, it)
                }
            }
        }
    }

    override fun getRecipeOutput() = ItemStack(ModItems.itemRecord)

    override fun getRemainingItems(inv: InventoryCrafting): NonNullList<ItemStack> {
        inv.clear()
        return NonNullList.create()
    }
}
