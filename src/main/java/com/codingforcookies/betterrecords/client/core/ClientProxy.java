package com.codingforcookies.betterrecords.client.core;

import com.codingforcookies.betterrecords.api.song.LibrarySong;
import com.codingforcookies.betterrecords.client.core.handler.BetterEventHandler;
import com.codingforcookies.betterrecords.client.core.handler.TESRRenderHandler;
import com.codingforcookies.betterrecords.client.render.*;
import com.codingforcookies.betterrecords.client.sound.SoundHandler;
import com.codingforcookies.betterrecords.common.BetterRecords;
import com.codingforcookies.betterrecords.common.block.ModBlocks;
import com.codingforcookies.betterrecords.common.block.tile.*;
import com.codingforcookies.betterrecords.common.core.CommonProxy;
import com.codingforcookies.betterrecords.common.item.ModItems;
import com.codingforcookies.betterrecords.common.lib.StaticInfo;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.lwjgl.input.Keyboard;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

public class ClientProxy extends CommonProxy {
    public static ClientProxy instance;
    public static Configuration config;

    public static KeyBinding keyConfig;

    /**
     * Last checked:
     *   0 = Unchecked
     *   1 = Singleplayer
     *   2 = Multiplayer
     */
    public static int lastCheckType = 0;
    public static File localLibrary;

    public static String defaultLibraryURL = "";
    public static ArrayList<LibrarySong> defaultLibrary;
    public static ArrayList<String> encodings;

    public static HashMap<String, Boolean> tutorials;

    public static boolean checkForUpdates = true;
    public static boolean hasCheckedForUpdates = false;

    public static boolean devMode = false;

    public static boolean playWhileDownload = false;
    public static int downloadMax = 10;
    public static int flashyMode = -1;

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);

        defaultLibrary = new ArrayList<LibrarySong>();
        encodings = new ArrayList<String>();
        encodings.add("audio/ogg");
        encodings.add("application/ogg");
        encodings.add("audio/mpeg");
        encodings.add("audio/mpeg; charset=UTF-8");
        encodings.add("application/octet-stream");
        encodings.add("audio/wav");
        encodings.add("audio/x-wav");

        tutorials = new HashMap<String, Boolean>();
        tutorials.put("recordplayer", false);
        tutorials.put("speaker", false);
        tutorials.put("radio", false);
        tutorials.put("strobelight", false);
        tutorials.put("lazer", false);
        tutorials.put("lazercluster", false);

        // Temporary way to render TESR's in the inventory. Should be changed once the JSON format supports animations
        registerTESRRender(ModBlocks.blockRecordEtcher, new BlockRecordEtcherRenderer(), TileEntityRecordEtcher.class, "recordetcher");
        registerTESRRender(ModBlocks.blockRecordPlayer, new BlockRecordPlayerRenderer(), TileEntityRecordPlayer.class, "recordplayer");
        registerTESRRender(ModBlocks.blockFrequencyTuner, new BlockFrequencyTunerRenderer(), TileEntityFrequencyTuner.class, "frequencytuner");
        registerTESRRender(ModBlocks.blockRadio, new BlockRadioRenderer(), TileEntityRadio.class, "radio");
        registerTESRRender(ModBlocks.blockSMSpeaker, new BlockRecordSpeakerRenderer(), TileEntityRecordSpeaker.class, "speaker.sm");
        registerTESRRender(ModBlocks.blockMDSpeaker, new BlockRecordSpeakerRenderer(), TileEntityRecordSpeaker.class, "speaker.md");
        registerTESRRender(ModBlocks.blockLGSpeaker, new BlockRecordSpeakerRenderer(), TileEntityRecordSpeaker.class, "speaker.lg");
        registerTESRRender(ModBlocks.blockStrobeLight, new BlockStrobeLightRenderer(), TileEntityStrobeLight.class, "strobelight");
        registerTESRRender(ModBlocks.blockLazer, new BlockLazerRenderer(), TileEntityLazer.class, "lazer");
        registerTESRRender(ModBlocks.blockLazerCluster, new BlockLazerClusterRenderer(), TileEntityLazerCluster.class, "lazercluster");
        MinecraftForge.EVENT_BUS.register(new TESRRenderHandler());


        SoundHandler.initalize();
        loadConfig(event);
    }

    private void registerTESRRender(Block block, TileEntitySpecialRenderer renderer,  Class<? extends TileEntity> te, String name) {
        ClientRegistry.bindTileEntitySpecialRenderer(te, renderer);
        Item item = Item.getItemFromBlock(block);
        ForgeHooksClient.registerTESRItemStack(item, 0, te);
        ModelLoader.setCustomStateMapper(block, new StateMap.Builder().ignore(StaticInfo.CARDINAL_DIRECTIONS).build());
        ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(BetterRecords.ID + ":itemblock/" + name, "inventory"));
    }


    @Override
    public void init(FMLInitializationEvent event) {
        instance = this;

        keyConfig = new KeyBinding("key.betterconfig.desc", Keyboard.KEY_N, "key.betterconfig.category");
        ClientRegistry.registerKeyBinding(keyConfig);

        localLibrary = new File(Minecraft.getMinecraft().mcDataDir, "betterrecords/localLibrary.json");

        if(!localLibrary.exists())
            try {
                localLibrary.createNewFile();

                BufferedWriter writer = null;
                try {
                    writer = new BufferedWriter(new FileWriter(ClientProxy.localLibrary));
                    writer.write("{}");
                } finally {
                        writer.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        MinecraftForge.EVENT_BUS.register(new BetterEventHandler());

        RenderItem renderItem = Minecraft.getMinecraft().getRenderItem();
        renderItem.getItemModelMesher().register(ModItems.itemFreqCrystal, 0, new ModelResourceLocation(BetterRecords.ID + ":" + "freqcrystal", "inventory"));
        renderItem.getItemModelMesher().register(ModItems.itemRecordWire, 0, new ModelResourceLocation(BetterRecords.ID + ":" + "recordwire", "inventory"));
        renderItem.getItemModelMesher().register(ModItems.itemRecordCutters, 0, new ModelResourceLocation(BetterRecords.ID + ":" + "recordwirecutters", "inventory"));
        renderItem.getItemModelMesher().register(ModItems.itemURLMultiRecord, 0, new ModelResourceLocation(BetterRecords.ID + ":" + "urlmultirecord", "inventory"));
        renderItem.getItemModelMesher().register(ModItems.itemURLRecord, 0, new ModelResourceLocation(BetterRecords.ID + ":" + "urlrecord", "inventory"));
    }

    @Override
    public void postInit(FMLPostInitializationEvent event) {

    }

    public static void loadConfig(FMLPreInitializationEvent event) {
        config = new Configuration(event.getSuggestedConfigurationFile());
        config.load();

        SoundHandler.downloadSongs = config.get(Configuration.CATEGORY_GENERAL, "downloadSongs", true).getBoolean(true);
        downloadMax = config.get(Configuration.CATEGORY_GENERAL, "downloadMax", 10).getInt(10);
        playWhileDownload = config.get(Configuration.CATEGORY_GENERAL, "playWhileDownload", false).getBoolean(false);
        SoundHandler.streamBuffer = config.get(Configuration.CATEGORY_GENERAL, "streamBuffer", 1024).getInt(1024);

        SoundHandler.streamRadio = config.get(Configuration.CATEGORY_GENERAL, "streamRadio", true).getBoolean(true);

        flashyMode = config.get(Configuration.CATEGORY_GENERAL, "flashyMode", -1).getInt(-1);

        for(Entry<String, Boolean> entry : tutorials.entrySet())
            entry.setValue(config.get("tutorials", entry.getKey(), false).getBoolean(false));

        devMode = config.get("other", "devMode", false).getBoolean(false);

        defaultLibraryURL = config.get("other", "defaultLibrary", "https://raw.githubusercontent.com/stumblinbear/Versions/master/betterrecords/defaultlibrary.json").getString();

        if(defaultLibraryURL.equals("http://files.enjin.com/788858/SBear'sMods/defaultlibrary.json"))
            defaultLibraryURL = "https://raw.githubusercontent.com/stumblinbear/Versions/master/betterrecords/defaultlibrary.json";
            config.get("other", "defaultLibrary", "https://raw.githubusercontent.com/stumblinbear/Versions/master/betterrecords/defaultlibrary.json").set("https://raw.githubusercontent.com/stumblinbear/Versions/master/betterrecords/defaultlibrary.json");

        config.save();
    }

    public static void saveConfig() {
        config.get(Configuration.CATEGORY_GENERAL, "downloadSongs", true).set(SoundHandler.downloadSongs);
        config.get(Configuration.CATEGORY_GENERAL, "downloadMax", 10).set(downloadMax);
        config.get(Configuration.CATEGORY_GENERAL, "playWhileDownload", false).set(playWhileDownload);
        config.get(Configuration.CATEGORY_GENERAL, "streamBuffer", false).set(SoundHandler.streamBuffer);

        config.get(Configuration.CATEGORY_GENERAL, "streamRadio", true).set(SoundHandler.streamRadio);

        config.get(Configuration.CATEGORY_GENERAL, "flashyMode", -1).set(flashyMode);

        for(Entry<String, Boolean> entry : tutorials.entrySet())
            config.get("tutorials", entry.getKey(), false).set(entry.getValue());

        config.get("other", "devMode", false).set(devMode);

        config.get("other", "defaultLibrary", "https://raw.githubusercontent.com/stumblinbear/Versions/master/betterrecords/defaultlibrary.json").set(defaultLibraryURL);

        config.save();
    }
}
