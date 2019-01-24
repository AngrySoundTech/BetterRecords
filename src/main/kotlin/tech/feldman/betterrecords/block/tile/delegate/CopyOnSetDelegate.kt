package tech.feldman.betterrecords.block.tile.delegate

import net.minecraft.item.ItemStack
import kotlin.reflect.KProperty

class CopyOnSetDelegate {

    private var field: ItemStack = ItemStack.EMPTY

    operator fun getValue(thisRef: Any?, property: KProperty<*>) = field
    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: ItemStack) {
        field = value.copy()
    }
}
