package pistonmc.pistonandobserver.core;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.registry.GameRegistry;
import it.unimi.dsi.fastutil.objects.Reference2CharMap;
import it.unimi.dsi.fastutil.ints.Int2CharArrayMap;
import it.unimi.dsi.fastutil.ints.Int2CharMap;
import libpiston.config.ConfigCategoryContainer;
import libpiston.config.ConfigCategoryFactory;
import libpiston.config.ConfigFactory;
import libpiston.config.ConfigRoot;
import libpiston.config.StringListConfigContainer;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import pistonmc.pistonandobserver.ModInfo;
import pistonmc.pistonandobserver.ModObjects;

public class Config {
    public static final String CONFIG_NAME = "PistonAndObserver.cfg";

    public static final String SLIME_BLOCK_NAME = "slime_block";
    public static final String HONEY_BLOCK_NAME = "honey_block";

    public static boolean enablePiston = true;
    public static boolean modifyVanillaPiston = true;
    public static int pistonMoveLimit = 12;
    public static boolean enableDebugBlocks = true;
    public static StringListConfigContainer stickyBlocks;
    public static Map<Block, Int2CharMap> blockToStickyTypes;

    public static boolean enableObserver = true;
    public static boolean enableWorldTweaks = true;
    public static boolean enableObserverBlock = true;

    public static void preInit() {
		File configFile = new File("").getAbsoluteFile().toPath().resolve("config").resolve(CONFIG_NAME).toFile();

        ConfigRoot root = new ConfigRoot(configFile);
        root.load();
        ConfigCategoryFactory categoryFactory = new ConfigCategoryFactory(root);
        {
            ConfigCategoryContainer category = categoryFactory.create("Master", "Master switches");
            ConfigFactory factory = new ConfigFactory(category);
            enablePiston = factory.createBoolean(
                "EnablePiston", 
                "Master switch to enable sticky blocks when moved by pistons", 
                true).get();
            enableObserver = factory.createBoolean(
                "EnableObserver", 
                "Master switch to enable the Observer API and mechanics",
                true).get();
        }
        {
            ConfigCategoryContainer category = categoryFactory.create("Piston", "Config for Pistons");
            ConfigFactory factory = new ConfigFactory(category);
            modifyVanillaPiston = factory.createBoolean(
                "ModifyVanillaPiston", 
                "Setting this to true will modify vanilla pistons to support sticky blocks. Otherwise, modified pistons will be added as new blocks. You should leave this on unless there are conflicts.", 
                true).get();
            pistonMoveLimit = factory.createInteger(
                "PistonMoveLimit", 
                "The maximum number of blocks a piston can move. If modifyVanillaPiston is false, this will only affect modified pistons",
                12).get();
            enableDebugBlocks = factory.createBoolean(
                "EnableDebugBlocks", 
                "Setting this to true will add slime and honey blocks from this mod without recipes. Leave it false if there are other mods that adds sticky blocks.", 
                !(isCompatibleSlimeBlockDetected() && isCompatibleHoneyBlockDetected())).get();
            stickyBlocks = factory.createStringList(
                "StickyBlocks", 
                "A list of blocks in the format of <TYPE>|<MODID>:<BLOCKNAME>:<META>. Type must a single character (rest is ignored). When meta is not specified it will match any meta. Sticky blocks of different types will not sticky to each other.",
                new String[] {
                    "Slime|" + ModInfo.MODID + ":" + SLIME_BLOCK_NAME,
                    "Honey|" + ModInfo.MODID + ":" + HONEY_BLOCK_NAME,
                    "Slime|TConstruct:slime.gel:1",
                    "BlueSlime|TConstruct:slime.gel:0",
                    "Honey|BiomesOPlenty:honeyBlock",
                });
        }
        {
            ConfigCategoryContainer category = categoryFactory.create("Observer", "Config for Observers");
            ConfigFactory factory = new ConfigFactory(category);
            enableWorldTweaks = factory.createBoolean(
                "EnableWorldTweaks", 
                "Setting this to false will only enable the Observer API. Most generic block updates will not update observers",
                true).get();
            enableObserverBlock = factory.createBoolean(
                "EnableObserverBlock",
                "Setting this to false will disable the observer block from this mod. The API will still work and notify observers from other mods",
                true).get();
        }
    }

    public static boolean isCompatibleSlimeBlockDetected() {
        return Loader.isModLoaded("TConstruct");
    }

    public static boolean isCompatibleHoneyBlockDetected() {
        return Loader.isModLoaded("BiomesOPlenty");
    }

    public static void postInit() {
        blockToStickyTypes = new HashMap<>();
        String[] stickyBlocksArray = stickyBlocks.get();
        for (String blockString : stickyBlocksArray) {
            String[] blockStringSplit = blockString.split("\\|", 2);
            if (blockStringSplit.length != 2 || blockStringSplit[0].isEmpty()) {
                System.out.println("Invalid sticky block string: " + blockString + ". Skipping");
                continue;
            }
            char type = blockStringSplit[0].charAt(0);
            if (type == (char)0) {
                System.out.println("Null char cannot be used as sticky type. Skipping");
                continue;
            }
            String[] blockNameSplit = blockStringSplit[1].split(":");
            if (blockNameSplit.length < 2 || blockNameSplit.length > 3) {
                System.out.println("Invalid block: " + blockStringSplit[1] + ". Skipping");
                continue;
            }
            String modid = blockNameSplit[0];
            String blockName = blockNameSplit[1];
            int meta = blockNameSplit.length == 3 ? Integer.parseInt(blockNameSplit[2]) : -1;
            Block block = GameRegistry.findBlock(modid, blockName);
            if (block == null || block == Blocks.air) {
                System.out.println("Block not found: " + blockStringSplit[1] + ". Skipping");
                continue;
            }
            Int2CharMap stickyTypes = blockToStickyTypes.get(block);
            if (stickyTypes == null) {
                stickyTypes = new Int2CharArrayMap();
                blockToStickyTypes.put(block, stickyTypes);
            }
            stickyTypes.put(meta, type);
            System.out.println("Added sticky block: " + blockStringSplit[1] + ". Type=" + type + "");
        }
    }

    public static final char NOT_STICKY = (char)0;
    /**
     * Get the sticky type of a block. Returns NOT_STICKY if not a sticky block
     */
    public static char getStickyType(Block block, int meta) {
        Int2CharMap stickyTypes = blockToStickyTypes.get(block);
        if (stickyTypes == null) {
            return NOT_STICKY;
        }
        char type = stickyTypes.getOrDefault(meta, NOT_STICKY);
        if (type == NOT_STICKY) {
            type = stickyTypes.getOrDefault(-1, NOT_STICKY);
        }
        return type;
    }

    public static boolean shouldModifyPiston(Object pistonThis) {
        return modifyVanillaPiston || pistonThis == ModObjects.modified_piston || pistonThis == ModObjects.modified_sticky_piston;
    }
}
