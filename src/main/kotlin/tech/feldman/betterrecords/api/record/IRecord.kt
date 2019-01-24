package tech.feldman.betterrecords.api.record

import tech.feldman.betterrecords.api.wire.IRecordWireHome
import net.minecraft.item.ItemStack

interface IRecord {

    fun isRecordValid(itemStack: ItemStack): Boolean

    fun onRecordInserted(wireHome: IRecordWireHome, itemStack: ItemStack)
}
