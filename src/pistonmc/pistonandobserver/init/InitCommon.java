package pistonmc.pistonandobserver.init;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.BlockPistonBase;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.oredict.ShapedOreRecipe;
import pistonmc.pistonandobserver.ModObjects;
import pistonmc.pistonandobserver.core.Config;
import pistonmc.pistonandobserver.observer.BlockObserver;
import pistonmc.pistonandobserver.observer.handler.ObserveGrassBlock;
import pistonmc.pistonandobserver.piston.BlockSticky;

public class InitCommon implements Init {
	@Override
	public void preInit(FMLPreInitializationEvent event) {
        Config.init();
        if (Config.enablePiston) {
            if (Config.enableSlimeBlock) {
                GameRegistry.registerBlock(new BlockSticky(Config.SLIME_BLOCK_NAME, false), Config.SLIME_BLOCK_NAME);
            }
            if (Config.enableHoneyBlock) {
                GameRegistry.registerBlock(new BlockSticky(Config.HONEY_BLOCK_NAME, true), Config.HONEY_BLOCK_NAME);
            }
            if (!Config.modifyVanillaPiston) {
                GameRegistry.registerBlock(new BlockPistonBase(false).setBlockName("modified_piston"), "modified_piston");
                GameRegistry.registerBlock(new BlockPistonBase(true).setBlockName("modified_sticky_piston"), "modified_sticky_piston");
            }
        }
        if (Config.enableObserver) {
            if (Config.enableObserverBlock) {
                GameRegistry.registerBlock(new BlockObserver(), "observer");
            }
        }
	}

	@Override
	public void init(FMLInitializationEvent event) {
        if (Config.enableObserverRecipe) {
            GameRegistry.addRecipe(new ShapedOreRecipe(ModObjects.observer, true, "AAA", "BBC", "AAA", 'A',
                "cobblestone", 'B', "dustRedstone", 'C', "gemQuartz"));
        }

	}

	@Override
	public void postInit(FMLPostInitializationEvent event) {
        Config.postInit();
        if (Config.enableObserver) {
            if (Config.enableObserveSnowedGrass) {
                MinecraftForge.EVENT_BUS.register(new ObserveGrassBlock());
            }
        }
	}
}
