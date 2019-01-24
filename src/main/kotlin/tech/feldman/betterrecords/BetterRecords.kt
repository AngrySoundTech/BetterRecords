package tech.feldman.betterrecords

import tech.feldman.betterrecords.item.ModItems
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.item.ItemStack
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.SidedProxy
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

@Mod(modid = tech.feldman.betterrecords.ID, name = tech.feldman.betterrecords.NAME, version = tech.feldman.betterrecords.VERSION, modLanguageAdapter = tech.feldman.betterrecords.LANGUAGE_ADAPTER, dependencies = tech.feldman.betterrecords.DEPENDENCIES)
object BetterRecords {

    val logger: Logger = LogManager.getLogger(tech.feldman.betterrecords.ID)

    @SidedProxy(clientSide = tech.feldman.betterrecords.CLIENT_PROXY, serverSide = tech.feldman.betterrecords.SERVER_PROXY)
    lateinit var proxy: tech.feldman.betterrecords.CommonProxy

    val creativeTab = object : CreativeTabs(tech.feldman.betterrecords.ID) {
        override fun getTabIconItem() = ItemStack(ModItems.itemRecord)
    }

    @Mod.EventHandler
    fun preInit(event: FMLPreInitializationEvent) = tech.feldman.betterrecords.BetterRecords.proxy.preInit(event)

    @Mod.EventHandler
    fun init(event: FMLInitializationEvent) = tech.feldman.betterrecords.BetterRecords.proxy.init(event)

    @Mod.EventHandler
    fun postInit(event: FMLPostInitializationEvent) = tech.feldman.betterrecords.BetterRecords.proxy.postInit(event)
}
