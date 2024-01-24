package pistonmc.pistonandobserver.core;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.registry.GameRegistry;
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

    private static ConfigCategoryContainer pistonCategory;
    private static ConfigCategoryContainer observerCategory;

    public static boolean enablePiston = true;
    public static boolean modifyVanillaPiston = false;
    public static int pistonMoveLimit = 12;
    public static boolean enableSlimeBlock = false;
    public static boolean enableHoneyBlock = false;
    public static StringListConfigContainer stickyBlocks;
    public static Map<Block, Int2CharMap> blockToStickyTypes = new HashMap<>();

    public static boolean enableObserver = true;
    public static boolean enableObserverBlock = false;
    public static boolean enableWorldTweaks = false;
    public static int observerDelay = 1;
    public static int observerDuration = 2;
    public static boolean enableObserverRecipe = false;

    public static boolean enableDoorTweaks = false;
    public static boolean enableObserveSnowedGrass = false;
    public static boolean enableNoteBlockTweaks = false;

    public static void preInit() {
        ModInfo.log.info("Loading configs - phase 1");
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
        // load configs needed by mixins here
        if (enablePiston) {
            pistonCategory = categoryFactory.create("Piston", "Config for Pistons");
            ConfigFactory factory = new ConfigFactory(pistonCategory);
            modifyVanillaPiston = factory.createBoolean(
                "ModifyVanillaPiston", 
                "Setting this to true will modify vanilla pistons to support sticky blocks. Otherwise, modified pistons will be added as new blocks. You should leave this on unless there are conflicts.", 
                true).get();
        }
        if (enableObserver) {
            observerCategory = categoryFactory.create("Observer", "Config for Observers");
            ConfigFactory factory = new ConfigFactory(observerCategory);
            enableWorldTweaks = factory.createBoolean(
                "EnableWorldTweaks", 
                "Setting this to false will only enable the Observer API. Most generic block updates will not update observers",
                true).get();
            enableDoorTweaks = factory.createBoolean(
                "EnableDoorTweaks", 
                "Enable mixin to vanilla doors to make them update observers correctly.",
                true).get();
            enableNoteBlockTweaks = factory.createBoolean(
                "EnableNoteBlockTweaks", 
                "Enable mixin to vanilla note blocks to update observers when their pitch, instrument, or redstone power changes.",
                true).get();
        }
    }

    public static void init() {
        ModInfo.log.info("Loading configs - phase 2");
        if (enablePiston) {
            ConfigFactory factory = new ConfigFactory(pistonCategory);
            pistonMoveLimit = factory.createInteger(
                "PistonMoveLimit", 
                "The maximum number of blocks a piston can move. If modifyVanillaPiston is false, this will only affect modified pistons",
                pistonMoveLimit).get();
            boolean compatibleSlimeBlockDetected = isCompatibleSlimeBlockDetected();
            if (compatibleSlimeBlockDetected) {
                ModInfo.log.info("Detected compatible slime block");
            }
            enableSlimeBlock = factory.createBoolean(
                "EnableSlimeBlock", 
                "Add a slime block implementation from this mod.", 
                !compatibleSlimeBlockDetected).get();
            boolean compatibleHoneyBlockDetected = isCompatibleHoneyBlockDetected();
            if (compatibleHoneyBlockDetected) {
                ModInfo.log.info("Detected compatible honey block");
            }
            enableHoneyBlock = factory.createBoolean(
                "EnableHoneyBlock", 
                "Add a honey block implementation from this mod.", 
                !compatibleHoneyBlockDetected).get();
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
        if (enableObserver) {
            ConfigFactory factory = new ConfigFactory(observerCategory);
            enableObserverBlock = factory.createBoolean(
                "EnableObserverBlock",
                "Setting this to false will disable the observer block from this mod. The API will still work and notify observers from other mods",
                true).get();
            observerDuration = factory.createInteger(
                "ObserverDuration",
                "How long the observer pulse lasts. Setting this to > 3 will cause sticky pistons to always retract their blocks. Default is close to vanilla",
                observerDuration).get();
            observerDelay = factory.createInteger(
                "ObserverDelay",
                "Time between observer gets update and when it emits a pulse. 0 means it will emit pulse on the same tick. Default is vanilla",
                observerDelay).get();
            enableObserverRecipe = factory.createBoolean(
                "EnableObserverRecipe",
                "Should the vanilla observer recipe be added",
                true).get();
            enableObserveSnowedGrass = factory.createBoolean(
                "EnableObserveSnowedGrass",
                "Should grass blocks changing between snowed and non-snowed cause observer update.",
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
        ModInfo.log.info("Loading configs - phase 3");
        initStickyBlocks();
        // allow GC
        stickyBlocks = null;
        pistonCategory = null;
        observerCategory = null;
    }

    private static void initStickyBlocks() {
        if (!enablePiston) {
            return;
        }
        String[] stickyBlocksArray = stickyBlocks.get();
        for (String blockString : stickyBlocksArray) {
            String[] blockStringSplit = blockString.split("\\|", 2);
            if (blockStringSplit.length != 2 || blockStringSplit[0].isEmpty()) {
                ModInfo.log.error("Invalid sticky block string: " + blockString + ". Skipping");
                continue;
            }
            char type = blockStringSplit[0].charAt(0);
            if (type == (char)0) {
                ModInfo.log.error("Null char cannot be used as sticky type. Skipping");
                continue;
            }
            String[] blockNameSplit = blockStringSplit[1].split(":");
            if (blockNameSplit.length < 2 || blockNameSplit.length > 3) {
                ModInfo.log.error("Invalid block: " + blockStringSplit[1] + ". Skipping");
                continue;
            }
            String modid = blockNameSplit[0];
            String blockName = blockNameSplit[1];
            int meta = blockNameSplit.length == 3 ? Integer.parseInt(blockNameSplit[2]) : -1;
            Block block = GameRegistry.findBlock(modid, blockName);
            if (block == null || block == Blocks.air) {
                ModInfo.log.error("Block not found: " + blockStringSplit[1] + ". Skipping");
                continue;
            }
            Int2CharMap stickyTypes = blockToStickyTypes.get(block);
            if (stickyTypes == null) {
                stickyTypes = new Int2CharArrayMap();
                blockToStickyTypes.put(block, stickyTypes);
            }
            stickyTypes.put(meta, type);
            ModInfo.log.info("Added sticky block: " + blockStringSplit[1] + ". Type=" + type + "");
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
