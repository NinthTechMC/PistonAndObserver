package pistonmc.pistonandobserver.init;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import pistonmc.pistonandobserver.core.Config;
import pistonmc.pistonandobserver.piston.BlockPistonAir;
import pistonmc.pistonandobserver.piston.BlockSticky;
import pistonmc.pistonandobserver.piston.TileEntityPistonAir;

public class InitCommon implements Init {
	@Override
	public void preInit(FMLPreInitializationEvent event) {
        Config.preInit();
        if (Config.enableDebugBlocks) {
            GameRegistry.registerBlock(new BlockSticky(Config.SLIME_BLOCK_NAME, false), Config.SLIME_BLOCK_NAME);
            GameRegistry.registerBlock(new BlockSticky(Config.HONEY_BLOCK_NAME, true), Config.HONEY_BLOCK_NAME);
        }
        if (!Config.modifyVanillaPiston) {
            GameRegistry.registerBlock(new BlockPistonBase(false).setBlockName("modified_piston"), "modified_piston");
            GameRegistry.registerBlock(new BlockPistonBase(true).setBlockName("modified_sticky_piston"), "modified_sticky_piston");
        }
		// GameRegistry.registerBlock(new BlockObserver(), "blockObserver");
		GameRegistry.registerBlock(new BlockPistonAir(), "piston_air");

		// GameRegistry.registerTileEntity(TileEntityObserver.class, "tileObserver");
		GameRegistry.registerTileEntity(TileEntityPistonAir.class, "tilePistonAir");

		// GameRegistry.registerItem(new ItemHoneyBall(), "itemHoneyBall");

	}

	@Override
	public void init(FMLInitializationEvent event) {
		// add recipes
		// GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(ModObjects.itemHoneyBall, 2), Items.slime_ball,
		// 		Items.slime_ball, Items.sugar, "dyeYellow"));
		// String[] shape = { "ABA", "BAB", "ABA" };
		// GameRegistry.addRecipe(new ShapedOreRecipe(ModObjects.blockPistonConnectorSlime, shape, 'A', "plankWood", 'B',
		// 		Items.slime_ball));
		// GameRegistry.addRecipe(new ShapedOreRecipe(ModObjects.blockPistonConnectorHoney, shape, 'A', "plankWood", 'B',
		// 		ModObjects.itemHoneyBall));
		// GameRegistry.addRecipe(new ShapedOreRecipe(ModObjects.blockObserver, true, "AAA", "BBC", "AAA", 'A',
		// 		"cobblestone", 'B', "dustRedstone", 'C', "gemQuartz"));

	}

	@Override
	public void postInit(FMLPostInitializationEvent event) {
        Config.postInit();
	}
}
