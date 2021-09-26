package com.hemogoblins.betterrecords;

import com.hemogoblins.betterrecords.proxy.ClientProxy;
import com.hemogoblins.betterrecords.proxy.CommonProxy;
import com.hemogoblins.betterrecords.proxy.Proxy;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(BetterRecords.MOD_ID)
public class BetterRecords {

    public static final String MOD_ID = "betterrecords";

    public static Proxy proxy;

    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    public BetterRecords() {
        //noinspection deprecation
        DistExecutor.callWhenOn(Dist.CLIENT, () -> () -> proxy = new ClientProxy());
        //noinspection deprecation
        DistExecutor.callWhenOn(Dist.DEDICATED_SERVER, () -> () -> proxy = new CommonProxy());

        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, com.hemogoblins.betterrecords.Config.CLIENT_SPEC);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, com.hemogoblins.betterrecords.Config.COMMON_SPEC);

        proxy.onConstruct();
    }
}
