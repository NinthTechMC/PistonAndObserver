package com.piston.mc.flyingmachinebackport.init;

import com.piston.mc.flyingmachinebackport.BlockConnectorHoney;
import com.piston.mc.flyingmachinebackport.BlockConnectorSlime;
import com.piston.mc.flyingmachinebackport.ItemHoneyBall;
import com.piston.mc.flyingmachinebackport.ModConfig;
import com.piston.mc.flyingmachinebackport.ModObjects;
import com.piston.mc.flyingmachinebackport.observer.BlockObserver;
import com.piston.mc.flyingmachinebackport.observer.TileEntityObserver;
import com.piston.mc.flyingmachinebackport.piston.BlockPistonAir;
import com.piston.mc.flyingmachinebackport.piston.TileEntityPistonAir;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

public class InitCommon implements Init {
	@Override
	public void preInit(FMLPreInitializationEvent event) {
		GameRegistry.registerBlock(new BlockConnectorSlime(), "blockPistonConnectorSlime");
		GameRegistry.registerBlock(new BlockConnectorHoney(), "blockPistonConnectorHoney");
		GameRegistry.registerBlock(new BlockObserver(), "blockObserver");
		GameRegistry.registerBlock(new BlockPistonAir(), "blockPistonAir");

		GameRegistry.registerTileEntity(TileEntityObserver.class, "tileObserver");
		GameRegistry.registerTileEntity(TileEntityPistonAir.class, "tilePistonAir");

		GameRegistry.registerItem(new ItemHoneyBall(), "itemHoneyBall");

	}

	@Override
	public void init(FMLInitializationEvent event) {
		// add recipes
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(ModObjects.itemHoneyBall, 2), Items.slime_ball,
				Items.slime_ball, Items.sugar, "dyeYellow"));
		String[] shape = { "ABA", "BAB", "ABA" };
		GameRegistry.addRecipe(new ShapedOreRecipe(ModObjects.blockPistonConnectorSlime, shape, 'A', "plankWood", 'B',
				Items.slime_ball));
		GameRegistry.addRecipe(new ShapedOreRecipe(ModObjects.blockPistonConnectorHoney, shape, 'A', "plankWood", 'B',
				ModObjects.itemHoneyBall));
		GameRegistry.addRecipe(new ShapedOreRecipe(ModObjects.blockObserver, true, "AAA", "BBC", "AAA", 'A',
				"cobblestone", 'B', "dustRedstone", 'C', "gemQuartz"));

	}

	@Override
	public void postInit(FMLPostInitializationEvent event) {
		ModConfig.getInstance().finishInitialization();
	}
}
